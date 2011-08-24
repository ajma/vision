using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.Mvc;
using Vision.Models;

namespace Vision.Controllers
{
	public class GlassesController : Controller
	{
		VisionContext context = new VisionContext();

		[HttpPost]
		public ActionResult Add(Glasses glasses)
		{
			// assign number
            glasses.Group = (int)Math.Floor(glasses.OD_Spherical);
			var numbers = context.Glasses.Where(g => g.Group == glasses.Group).Select(g => g.Number).OrderBy(g => g).ToArray();
            glasses.Number = numbers.Length > 0 ? numbers.Max() + 1 : 1;
			for (int i = 0; i < numbers.Length; i++)
			{
				if (numbers[i] != i + 1)
				{
					glasses.Number = i + 1;
					break;
				}
			}

            // add date time stamp
            glasses.InsertDate = DateTime.UtcNow;

            context.Glasses.Add(glasses);
            context.SaveChanges();

			return Json(glasses);
		}

        [HttpPost]
        public ActionResult Search(Glasses search, int maxResults = 100)
        {
            var results = context.Glasses.Scoring(search);

            return Json(results.OrderByDescending(g => g.MatchScore).Take(maxResults));
        }

        [HttpPost]
        public ActionResult Remove(int group, int number)
        {
            var glasses = context.Glasses.Single(g => g.Group == group && g.Number == number);
            context.Glasses.Remove(glasses);
            var history = new GlassesHistory(glasses);
            history.RemovalDate = DateTime.UtcNow;
            context.GlassesHistory.Add(history);
            context.SaveChanges();

            return Json(true);
        }

        [HttpPost]
        public ActionResult History()
        {
            var history = context.GlassesHistory.OrderByDescending(h => h.RemovalDate).ToArray();
            return Json(history);
        }

        public static byte[] ConvertStringToBytes(string input)
        {
            MemoryStream stream = new MemoryStream();

            using (StreamWriter writer = new StreamWriter(stream))
            {
                writer.Write(input);
                writer.Flush();
            }

            return stream.ToArray();
        }


        public ActionResult ExportToKendallFormat()
        {
            var glasses = context.Glasses.OrderBy(g => new { g.OD_Spherical, g.OS_Spherical });

            StringBuilder sb = new StringBuilder();
            int i = 1;
            foreach (var g in glasses)
            {
                // example: 20,-3.25,-.50,107,,-3.25,.00,0,, 0,0,0,0,0,M, 0,Manually,8/31/2007,8:30:49 PM
                sb.AppendFormat("{0},{1},{2},{3},{4},{5},{6},{7},{8}, 0,0,0,0,0,{9}, 0,Manually,{10},{11}\r\n",
                    i++,
                    g.OD_Spherical, g.OD_Cylindrical, g.OD_Axis, g.OD_Add,
                    g.OS_Spherical, g.OS_Cylindrical, g.OS_Axis, g.OS_Add,
                    g.Size,
                    g.InsertDate.Date, g.InsertDate.TimeOfDay
                    );
            }

            return File(ConvertStringToBytes(sb.ToString()), "text/txt", "total_Inventory.txt");
        }
	}
}
