using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Vision.Models
{
    public class GlassesQuery
    {
        public float OD_Spherical { get; set; }
        public float OD_Cylindrical { get; set; }
        public int OD_Axis { get; set; }
        public float OD_Add { get; set; }

        public float OS_Spherical { get; set; }
        public float OS_Cylindrical { get; set; }
        public int OS_Axis { get; set; }
        public float OS_Add { get; set; }

        public bool Sunglasses { get; set; }

        public string Size { get; set; }

        public string Gender { get; set; }

        public bool OD_Blind { get; set; }
        public bool OS_Blind { get; set; }
    }
}