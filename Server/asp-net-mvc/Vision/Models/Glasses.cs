using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Vision.Models
{
    public class Glasses
    {
        public int GlassesId { get; set; }
        public short Group { get; set; }
        public int Number { get; set; }

        public bool OD_Blind { get; set; }
        public float OD_Spherical { get; set; }
        public float OD_Cylindrical { get; set; }
        public byte OD_Axis { get; set; }
        public float OD_Add { get; set; }

        public bool OS_Blind { get; set; }
        public float OS_Spherical { get; set; }
        public float OS_Cylindrical { get; set; }
        public byte OS_Axis { get; set; }
        public float OS_Add { get; set; }

        public bool? Sunglasses { get; set; }
        public char? Size { get; set; }
        public char? Gender { get; set; }

        public DateTime AddedDate { get; set; }
        public DateTime RemovedDate { get; set; }
        public string RemovedReason { get; set; }
        public string RemovedBy { get; set; }
    }
}