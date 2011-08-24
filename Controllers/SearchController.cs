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
        public ActionResult Query(Glasses search, int maxResults = 100)
        {
            var results = context.Glasses.FuzzySearch(search);

            return Json(results.OrderByDescending(g => g.MatchScore).Take(maxResults));
        }

    }
}
