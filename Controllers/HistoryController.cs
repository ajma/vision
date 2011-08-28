using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace Vision.Controllers
{
    public class HistoryController : Controller
    {
        VisionContext context = new VisionContext();

        public ActionResult History()
        {
            var history = context.GlassesHistory.OrderByDescending(h => h.RemovalDate).ToArray();
            return View(history);
        }

    }
}
