using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data.Entity;
using Vision.Models;

namespace Vision
{
    public class VisionContext : DbContext
    {
		public DbSet<Glasses> Glasses { get; set; }
        public DbSet<GlassesHistory> GlassesHistory { get; set; }
        public DbSet<GlassesBatch> GlassesBatches { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Glasses>().Map(m =>
            {
                m.MapInheritedProperties();
                m.ToTable("Glasses");
            }).HasKey(g => new { g.Group, g.Number });

            modelBuilder.Entity<GlassesHistory>().Map(m =>
            {
                m.MapInheritedProperties();
                m.ToTable("GlassesHistory");
            });
        }
    }
}