using System;
using System.Collections;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlServerCe;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.Caching;
using Dapper;
using Vision.Models;
using Vision.Search;

namespace Vision
{
    internal static class SqlHelper
    {
        private static string databaseLocation = null;
        private static string connectionString = null;

        #region Sql Statements
        private static readonly string tableExists = @"SELECT COUNT(TABLE_NAME) AS Count FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = @TableName";

        private static readonly string createGlassesTable = @"
            CREATE TABLE [Glasses] (
                [GlassesId] int NOT NULL IDENTITY,
                [OD_Blind] bit NOT NULL, 
                [OD_Spherical] real NOT NULL, 
                [OD_Cylindrical] real NOT NULL, 
                [OD_Axis] tinyint NOT NULL, 
                [OD_Add] real NOT NULL, 
                [OS_Blind] bit NOT NULL, 
                [OS_Spherical] real NOT NULL, 
                [OS_Cylindrical] real NOT NULL, 
                [OS_Axis] tinyint NOT NULL, 
                [OS_Add] real NOT NULL, 
                [Sunglasses] bit, 
                [Size] nvarchar(1), 
                [Gender] nvarchar(1), 
                [AddedDate] datetime NOT NULL,
                [RemovedDate] datetime,
                [RemovedReason] nvarchar(256),
                [RemovedBy] nvarchar(256),
            CONSTRAINT [PK_Glasses] PRIMARY KEY ([GlassesId]))
            ";

        private static readonly string createInventoryTable = @"
            CREATE TABLE [Inventory] (
                [Group] smallint NOT NULL, 
                [Number] int NOT NULL,
                [GlassesId] int NOT NULL,
            CONSTRAINT [PK_Inventory] PRIMARY KEY ([Group], [Number]))
            ";

        public static readonly string insertGlassesTable = @"
            INSERT INTO Glasses
                (OD_Blind,OD_Spherical,OD_Cylindrical,OD_Axis,OD_Add
                ,OS_Blind,OS_Spherical,OS_Cylindrical,OS_Axis,OS_Add
                ,AddedDate)
            VALUES
                (@OD_Blind,@OD_Spherical,@OD_Cylindrical,@OD_Axis,@OD_Add
                ,@OS_Blind,@OS_Spherical,@OS_Cylindrical,@OS_Axis,@OS_Add
                ,@AddedDate)
            ";
        #endregion

        public static void EnsureDbSetup()
        {
            if (databaseLocation == null)
                databaseLocation = System.Web.HttpContext.Current.Server.MapPath(@"~\App_Data\Vision.sdf");

            if (connectionString == null)
                connectionString = "Data Source=" + databaseLocation;

            // make sure database file is created
            if (!File.Exists(databaseLocation))
            {
                var engine = new SqlCeEngine(connectionString);
                engine.CreateDatabase();
            }

            using (var connection = GetConnection())
            {
                // make sure glasses table exists
                ensureTableExists(connection, "Glasses", createGlassesTable);
                ensureTableExists(connection, "Inventory", createInventoryTable);
            }
        }

        private static void ensureTableExists(IDbConnection connection, string tableName, string createStatement)
        {
            // make sure glasses table exists
            if (connection.Query(tableExists, new { TableName = tableName }).First().Count == 0)
                connection.Execute(createStatement);
        }

        public static IDbConnection GetConnection()
        {
            var connection = new SqlCeConnection(connectionString);
            connection.Open();

            return connection;
        }

        public static Glasses InsertGlasses(Glasses glasses)
        {
            using (var connection = GetConnection())
            {
                // figure out which group this pair goes to. the algorith for this is rounding the OD (right) Sph value away from 0
                bool positive = (glasses.OD_Spherical >= 0);
                glasses.Group = (short)Math.Ceiling(Math.Abs(glasses.OD_Spherical));
                // anything over 10 gets grouped into the 20 group
                if (glasses.Group > 10)
                    glasses.Group = 20;
                if (!positive)
                    glasses.Group *= -1;

                // grab all glasses in this group so that I can look for the next pair of glasses
                var numbers = connection.Query("SELECT MAX(Number) AS Count FROM Inventory WHERE [Group] = @Group", glasses);
                glasses.Number = numbers.First().Count == null ? 1 : numbers.First().Count + 1;
                
                // set added date to now
                glasses.AddedDate = DateTime.UtcNow;

                // insert glasses
                connection.Execute(insertGlassesTable, glasses);
                glasses.GlassesId = (int)connection.Query<decimal>("SELECT @@IDENTITY AS LastInsertedId").Single();
                // insert inventory
                connection.Execute("INSERT INTO Inventory ([Group], [Number], [GlassesId]) VALUES(@Group, @Number, @GlassesId)", glasses);

                // make sure cached inventory is up to date
                addtoCachedInventory(glasses);

                return glasses;
            }
        }

        public static void RemoveGlasses(int group, int number)
        {
            using (var connection = GetConnection())
            {
                int glassesId = connection.Query<int>("SELECT GlassesId FROM Inventory WHERE [Group] = @Group AND Number = @Number", 
                    new { Group = group, Number = number }).Single();

                connection.Execute("DELETE Inventory WHERE GlassesId = @GlassesId", new { GlassesId = glassesId });
                removeFromCachedInventory(group, number);
            }
        }

        public static Glasses GetGlassesByCallNumber(int group, int number)
        {
            using (var connection = GetConnection())
            {
                return connection.Query<Glasses>("SELECT * FROM Inventory, Glasses WHERE Inventory.GlassesId = Glasses.GlassesId AND [Group] = @Group AND Number = @Number",
                    new { Group = group, Number = number }).FirstOrDefault();
            }
        }

        public static IEnumerable<GlassesSearchResult> Search(Glasses rx)
        {
            IEnumerable<Glasses> inventory = getCachedInventory();

            GlassesSearchBase search = new MikeTamSearch();

            return search.Search(inventory, rx).Take(50);
        }

        #region Inventory Caching
        private static string GLASSES_INVENTORY_CACHE_KEY = "Glasses_Inventory";

        private static IEnumerable<Glasses> getCachedInventory()
        {
            List<Glasses> inventory = (List<Glasses>)HttpContext.Current.Cache[GLASSES_INVENTORY_CACHE_KEY];
            if (inventory == null)
            {
                using (var connection = GetConnection())
                {
                    inventory = connection.Query<Glasses>("SELECT * FROM Inventory, Glasses WHERE Inventory.GlassesId = Glasses.GlassesId").ToList();
                    HttpContext.Current.Cache.Add(GLASSES_INVENTORY_CACHE_KEY, inventory, null, Cache.NoAbsoluteExpiration, Cache.NoSlidingExpiration, CacheItemPriority.Default, null);
                }
            }
            return inventory;
        }

        private static void addtoCachedInventory(Glasses glasses)
        {
            List<Glasses> inventory = (List<Glasses>)HttpContext.Current.Cache[GLASSES_INVENTORY_CACHE_KEY];
            if (inventory != null)
            {
                inventory.Add(glasses);
                HttpContext.Current.Cache.Add(GLASSES_INVENTORY_CACHE_KEY, inventory, null, Cache.NoAbsoluteExpiration, Cache.NoSlidingExpiration, CacheItemPriority.Default, null);
            }
        }

        private static void removeFromCachedInventory(int group, int number)
        {
            List<Glasses> inventory = (List<Glasses>)HttpContext.Current.Cache[GLASSES_INVENTORY_CACHE_KEY];
            if (inventory != null)
            {
                Glasses remove = null;
                foreach (Glasses glasses in inventory)
                {
                    if (glasses.Group == group && glasses.Number == number)
                    {
                        remove = glasses;
                        break;
                    }

                }
                if (remove != null)
                    inventory.Remove(remove);

                HttpContext.Current.Cache.Add(GLASSES_INVENTORY_CACHE_KEY, inventory, null, Cache.NoAbsoluteExpiration, Cache.NoSlidingExpiration, CacheItemPriority.Default, null);
            }
        }
        #endregion
    }
}