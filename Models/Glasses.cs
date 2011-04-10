using System;
using System.ComponentModel.DataAnnotations;

namespace Vision.Models
{
	public class Glasses
	{
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