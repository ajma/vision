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
            var results = scoring(search);

            return Json(results.OrderByDescending(g => g.MatchScore).Take(maxResults));
        }

        private float scoreSpherical(float search, float glasses)
        {
            if (search >= 0 && search >= glasses && glasses >= 0)
            {
                return (search - glasses) / 0.25f;
            }
            else if (search < 0 && search <= glasses && glasses <= 0)
            {
                return (search - glasses) / -0.25f;
            }
            return 100f;
        }

        private float scoreCylindrical(float search, float glasses)
        {
            if (search - 0.25f <= glasses)
            {
                return Math.Abs(search - glasses) / 0.25f;
            }
            return 100f;
        }

        private float scoreAxis(float search_cyl, int search_axis, int glasses_axis)
        {
            int reasonableRange = 5;
            if (search_cyl >= -1.0f)
                reasonableRange = 20;
            else if (search_cyl >= -2.0f)
                reasonableRange = 15;
            else if (search_cyl >= -3.0f)
                reasonableRange = 10;

            int distance = Math.Abs(search_axis - glasses_axis);
            distance = Math.Min(distance, 180 - distance);

            if (distance < reasonableRange)
                return (8 * distance) / reasonableRange;
            else if (distance < reasonableRange * 2)
                return 15;
            else if (distance < reasonableRange * 3)
                return 25;
            else
                return 35;
        }

        // this is my second attempt at scoring after some consultation from Mike Tam.
        private List<GlassesSearchResult> scoring(Glasses search)
        {
            var results = new List<GlassesSearchResult>();
            foreach (var glasses in context.Glasses)
            {
                GlassesSearchResult result = new GlassesSearchResult { Glasses = glasses };
                result.MatchScore = 100f;
                StringBuilder details = new StringBuilder();

                /*** Calculate Cyl ***/
                float od_cyl_score = 2 * scoreCylindrical(search.OD_Cylindrical, glasses.OD_Cylindrical);
                result.MatchScore -= od_cyl_score;
                details.AppendFormat("OD Cyl: -{0}\n", od_cyl_score);

                float os_cyl_score = 2 * scoreCylindrical(search.OS_Cylindrical, glasses.OS_Cylindrical);
                result.MatchScore -= os_cyl_score;
                details.AppendFormat("OS Cyl: -{0}\n", os_cyl_score);

                // if the two eyes have an adjustment difference of more than 1 step, then penalize
                if (Math.Abs(od_cyl_score - os_cyl_score) > 2)
                {
                    result.MatchScore -= 5;
                    details.Append("OS OD cyl diff > 1 step: -5\n");
                }

                /*** Calculate Sph ***/
                float od_sph_score = 2 * scoreSpherical(search.OD_Spherical, glasses.OD_Spherical);
                result.MatchScore -= od_sph_score;
                details.AppendFormat("OD Sph: -{0}\n", od_sph_score);

                float os_sph_score = 2 * scoreSpherical(search.OS_Spherical, glasses.OS_Spherical);
                result.MatchScore -= os_sph_score;
                details.AppendFormat("OS Sph: -{0}\n", os_sph_score);

                // if the two eyes have an adjustment difference of more than 1 step, then penalize
                if (Math.Abs(od_sph_score - os_sph_score) > 2)
                {
                    result.MatchScore -= 5;
                    details.Append("OS OD sph diff > 1 step: -5\n");
                }

                // check if any of the properties are different more than one step
                float minStep = Math.Min(od_cyl_score, Math.Min(os_cyl_score, Math.Min(od_sph_score, os_sph_score)));
                if (od_cyl_score - minStep > 2
                    || os_cyl_score - minStep > 2
                    || od_sph_score - minStep > 2
                    || os_sph_score - minStep > 2)
                {
                    result.MatchScore -= 5;
                    details.Append("Cyl and/or sph diff > 1 step: -5\n");
                }

                // spherical equivalant (sph + cyl/2)
                if (od_sph_score > 0 && od_sph_score == od_cyl_score / 2)
                {
                    result.MatchScore += 7;
                    details.Append("OD Spherical equiv: +7\n");
                }
                if (os_sph_score > 0 && os_sph_score == os_cyl_score / 2)
                {
                    result.MatchScore += 7;
                    details.Append("OS Spherical equiv: +7\n");
                }

                /*** Calculate Axis ***/
                float od_axis_score = scoreAxis(search.OD_Cylindrical, search.OD_Axis, glasses.OD_Axis);
                result.MatchScore -= od_axis_score;
                details.AppendFormat("OD Axis: -{0}\n", od_axis_score);

                float os_axis_score = scoreAxis(search.OS_Cylindrical, search.OS_Axis, glasses.OS_Axis);
                result.MatchScore -= os_axis_score;
                details.AppendFormat("OS Axis: -{0}\n", os_axis_score);

                if (result.MatchScore >= 0)
                {
                    result.MatchScoreDetails = details.ToString();
                    results.Add(result);
                }
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
