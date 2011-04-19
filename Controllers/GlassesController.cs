using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Vision.Models;
using System.Text;
using System.IO;

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
            var results = scoringB(search);

            return Json(results.OrderByDescending(g => g.MatchScore).Take(maxResults));
        }

        // this is my first attempt at scoring. Pretty raw type of scoring
        private List<GlassesSearchResult> scoringA(Glasses search)
        {
            var results = new List<GlassesSearchResult>();
            foreach (var glasses in context.Glasses)
            {
                GlassesSearchResult result = new GlassesSearchResult { Glasses = glasses };

                float od_sph_match = 1.0f - (Math.Abs(glasses.OD_Spherical - search.OD_Spherical) / 20.0f);
                float od_cyl_match = 1.0f - (Math.Abs(glasses.OD_Cylindrical - search.OD_Cylindrical) / 20.0f);
                float od_axis_match = 1.0f - (Math.Abs(glasses.OD_Axis - search.OD_Axis) / 180.0f);
                float od_add_match = 1.0f - (Math.Abs(glasses.OD_Add - search.OD_Add) / 10.0f);

                float os_sph_match = 1.0f - (Math.Abs(glasses.OS_Spherical - search.OS_Spherical) / 20.0f);
                float os_cyl_match = 1.0f - (Math.Abs(glasses.OS_Cylindrical - search.OS_Cylindrical) / 20.0f);
                float os_axis_match = 1.0f - (Math.Abs(glasses.OS_Axis - search.OS_Axis) / 180.0f);
                float os_add_match = 1.0f - (Math.Abs(glasses.OS_Add - search.OS_Add) / 10.0f);

                result.MatchScore = (float)Math.Round((od_sph_match * od_cyl_match * od_axis_match * od_add_match * os_sph_match * os_cyl_match * os_axis_match * os_add_match) * 100.0f, 2);

                results.Add(result);
            }
            return results;
        }

        private float stepDifference(float search, float original)
        {
            if (search > 0 && search >= original)
            {
                return (search - original) / 0.25f;
            }
            else if (search <= 0 && search <= original)
            {
                return (search - original) / -0.25f;
            }
            return 100f;
        }

        private float axisSearch(float search_cyl, int search_axis, int glasses_axis)
        {
            int reasonableRange = 5;
            if (search_cyl <= -1.0f)
                reasonableRange = 20;
            else if (search_cyl <= -2.0f)
                reasonableRange = 15;
            else if (search_cyl <= -3.0f)
                reasonableRange = 10;

            int distance = Math.Abs(search_axis - glasses_axis);
            distance = Math.Min(distance, 180 - distance);

            if (distance < reasonableRange)
                return (10 * distance) / reasonableRange;
            else if (distance < reasonableRange * 2)
                return 15;
            else if (distance < reasonableRange * 3)
                return 25;
            else
                return 35;
        }

        // this is my second attempt at scoring after some consultation from Mike Tam.
        private List<GlassesSearchResult> scoringB(Glasses search)
        {
            var results = new List<GlassesSearchResult>();
            foreach (var glasses in context.Glasses)
            {
                GlassesSearchResult result = new GlassesSearchResult { Glasses = glasses };
                result.MatchScore = 100f;
                result.MatchScore -= 2 * stepDifference(search.OD_Cylindrical, glasses.OD_Cylindrical);
                result.MatchScore -= 2 * stepDifference(search.OS_Cylindrical, glasses.OS_Cylindrical);
                result.MatchScore -= stepDifference(search.OD_Spherical, glasses.OD_Spherical);
                result.MatchScore -= stepDifference(search.OS_Spherical, glasses.OS_Spherical);

                result.MatchScore -= axisSearch(search.OS_Cylindrical, search.OS_Axis, glasses.OS_Axis);
                result.MatchScore -= axisSearch(search.OD_Cylindrical, search.OD_Axis, glasses.OD_Axis);
                //float os_axis_diff = (search.OS_Axis - glasses.OS_Axis) % 180;
                //if (os_axis_diff < reasonableRange(search.OS_Cylindrical))
                //    result.MatchScore -= (10f * Math.Abs(search.OS_Axis - glasses.OS_Axis)) / reasonableRange(search.OS_Cylindrical);
                //else if (os_axis_diff < reasonableRange(search.OS_Cylindrical) * 2)
                //    result.MatchScore -= 15f;
                //else if (os_axis_diff < reasonableRange(search.OS_Cylindrical) * 2)
                //    result.MatchScore -= 25f;
                //else
                //    result.MatchScore -= 35f;

                //float od_axis_diff = (search.OD_Axis - glasses.OD_Axis) % 180;
                //if (od_axis_diff < reasonableRange(search.OD_Cylindrical))
                //    result.MatchScore -= (10f * Math.Abs(search.OD_Axis - glasses.OD_Axis)) / reasonableRange(search.OD_Cylindrical);
                //else if (od_axis_diff < reasonableRange(search.OD_Cylindrical) * 2)
                //    result.MatchScore -= 15f;
                //else if (od_axis_diff < reasonableRange(search.OD_Cylindrical) * 2)
                //    result.MatchScore -= 25f;
                //else
                //    result.MatchScore -= 35f;

                results.Add(result);
            }
            return results;
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
