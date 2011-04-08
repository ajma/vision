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
    }
}