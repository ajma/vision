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
        public ActionResult Add()
        {
            return View();
        }

        [HttpPost]
        public JsonResult Add(Glasses glasses)
        {
            return Json(new { Group = 2, Number = 3 });
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
            results.Add(new GlassesSearchResult { Score=1, Group = 1, Number = 2, OD = new Glasses.Eye { Spherical = 1, Cylindrical = -1, Axis = 5 }, OS = new Glasses.Eye { Spherical = 1, Cylindrical = -1, Axis = 100 } });
            results.Add(new GlassesSearchResult { Score = 2, Group = 1, Number = 2, OD = new Glasses.Eye { Spherical = 1, Cylindrical = -1, Axis = 11 }, OS = new Glasses.Eye { Spherical = 1, Cylindrical = -1, Axis = 10 } });
            return Json(results);
        }

        [HttpPost]
        public JsonResult SearchByCallNumber(int group, int number)
        {
            return Json(new Glasses { Group = 1, Number = 2, OD = new Glasses.Eye { Spherical = 1, Cylindrical = -1 }, OS = new Glasses.Eye { Spherical = 1, Cylindrical = -1 } });
        }
    }
}
