using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Vision.Models;

namespace Vision.Controllers
{
    public class SearchController : Controller
    {
        VisionContext context = new VisionContext();

        public ActionResult Search()
        {
            return View();
        }

        [HttpPost]
        public ActionResult FuzzySearch(GlassesQuery query, int maxResults = 100)
        {
            var results = context.Glasses.AsNoTracking().FuzzySearch(query);

            return Json(results.OrderByDescending(g => g.MatchScore).Take(maxResults));
        }

        [HttpPost]
        public ActionResult CallNumberSearch(int group, int number, int maxResults = 100)
        {
            var results = context.Glasses.AsNoTracking().CallNumberSearch(group, number);

            return Json(results.Take(maxResults));
        }
    }
}
