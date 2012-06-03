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
        public JsonResult SearchByRx(Glasses rx)
        {
            return Json(SqlHelper.Search(rx));
        }

        [HttpPost]
        public JsonResult SearchByCallNumber(short group, int number)
        {
            return Json(new GlassesSearchResult { Score = 100, Glasses = SqlHelper.GetGlassesByCallNumber(group, number) });
        }
    }
}
 
