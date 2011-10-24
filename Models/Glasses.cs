using System;
using System.ComponentModel.DataAnnotations;

namespace Vision.Models
{
	public class Glasses
	{
        public Glasses()
        {
        }

        public Glasses(GlassesHistory g)
        {
            Group = g.Group;
            Number = g.Number;

            OD_Spherical = g.OD_Spherical;
            OD_Cylindrical = g.OD_Cylindrical;
            OD_Axis = g.OD_Axis;
            OD_Add = g.OD_Add;

            OS_Spherical = g.OS_Spherical;
            OS_Cylindrical = g.OS_Cylindrical;
            OS_Axis = g.OS_Axis;
            OS_Add = g.OS_Add;

            Sunglasses = g.Sunglasses;
            Size = g.Size;
            Gender = g.Gender;

            InsertDate = g.InsertDate;
        }

		[Key, Column(Order = 0)]
		public int Group { get; set; }
		[Key, Column(Order = 1)]
		public int Number { get; set; }

		public float OD_Spherical { get; set; }
		public float OD_Cylindrical { get; set; }
		public int OD_Axis { get; set; }
		public float OD_Add { get; set; }

		public float OS_Spherical { get; set; }
		public float OS_Cylindrical { get; set; }
		public int OS_Axis { get; set; }
		public float OS_Add { get; set; }

		public bool Sunglasses { get; set; }

		[StringLength(1)]
		public string Size { get; set; }

		[StringLength(1)]
		public string Gender { get; set; }

        public DateTime InsertDate { get; set; }
	}
}