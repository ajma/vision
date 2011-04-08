using System;
using System.Collections.Generic;
using System.Linq;
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
		public ActionResult Search(Glasses search, int maxResults = 50)
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

			return Json(results.OrderByDescending(g => g.MatchScore).Take(maxResults));
		}

        [HttpPost]
        public ActionResult Remove(int group, int number)
        {
            var glasses = context.Glasses.Single(g => g.Group == group && g.Number == number);
            context.Glasses.Remove(glasses);
            context.SaveChanges();

            return Json(true);
        }
	}
}
