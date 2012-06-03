using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Vision.Models
{
    public class GlassesSearchResult
    {
        public float Score { get; set; }
        public string ScoreDetails { get; set; }
        public Glasses Glasses { get; set; }
    }
}