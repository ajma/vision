using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Vision.Models
{
    public class Glasses
    {
        public int GlassesId { get; set; }
        public int Group { get; set; }
        public int Number { get; set; }
        public Eye OD { get; set; }
        public Eye OS { get; set; }
        public bool Sunglasses { get; set; }
        public char Size { get; set; }
        public char Gender { get; set; }

        public class Eye
        {
            public float Spherical { get; set; }
            public float Cylindrical { get; set; }
            public int Axis { get; set; }
            public float Add { get; set; }
        }
    }
}