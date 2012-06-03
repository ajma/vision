using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Mvc;
using Vision.Models;

namespace Vision.Controllers
{
    public class GlassesController : Controller
    {
        public ActionResult Add()
        {
            return View();
        }

        [HttpPost]
        public JsonResult Add(Glasses glasses)
        {
            return Json(SqlHelper.InsertGlasses(glasses));
        }

        [HttpPost]
        public JsonResult Edit(Glasses glasses)
        {
            return Json(null);
        }

        [HttpPost]
        public JsonResult Remove(int glassesId, String by, String reason)
        {
            return Json(null);
        }

        public ActionResult Search()
        {
            return View();
        }

        [HttpPost]
        public JsonResult SearchByRx(Glasses glasses)
        {
            List<GlassesSearchResult> results = new List<GlassesSearchResult>();
            results.Add(new GlassesSearchResult { Score = 1, Group = 1, Number = 2, OD_Spherical = 1, OD_Cylindrical = -1, OD_Axis = 5, OS_Spherical = 1, OS_Cylindrical = -1, OS_Axis = 100 });
            results.Add(new GlassesSearchResult { Score = 2, Group = 1, Number = 2, OD_Spherical = 1, OD_Cylindrical = -1, OD_Axis = 11, OS_Spherical = 1, OS_Cylindrical = -1, OS_Axis = 10 });
            return Json(results);
        }

        [HttpPost]
        public JsonResult SearchByCallNumber(short group, int number)
        {
            return Json(SqlHelper.GetGlassesByCallNumber(group, number));
        }
    }
}
 
