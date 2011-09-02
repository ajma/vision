using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Text;
using Vision.Models;

namespace Vision.Controllers
{
    public class HistoryController : Controller
    {
        VisionContext context = new VisionContext();

        public ActionResult History()
        {
            return View();
        }

        public ActionResult RemovalHistory()
        {
            var history = context.GlassesHistory.OrderByDescending(h => h.RemovalDate).ToArray();
            return View(history);
        }

        public ActionResult InsertionHistory(int page = 0)
        {
            int pageSize = 200;
            var history = context.Glasses.OrderByDescending(h => h.InsertDate).Skip(page * pageSize).Take(pageSize).ToArray();
            return View(history);
        }

        [HttpPost]
        public ActionResult InsertionHistory(string[] callnum)
        {
            StringBuilder sb = new StringBuilder();
            sb.AppendLine("Group,Number,Call,L_Sp,L_Cy,L_Ax,L_Ad,R_Sp,R_Cy,R_Ax,R_Ad");

            // create dictionary for lookup
            var glasses = new Dictionary<string, Glasses>();
            foreach (var g in context.Glasses.OrderByDescending(h => h.InsertDate))
            {
                glasses.Add(g.Group + "/" + g.Number, g);
            }

            // output a line for each glasses requested
            foreach (var c in callnum)
            {
                var g = glasses[c];
                sb.Append(g.Group);
                sb.Append(',');
                sb.Append(g.Number);
                sb.Append(',');
                sb.AppendFormat("{0}/{1}", g.Group, g.Number);
                sb.Append(',');
                sb.Append(g.OS_Spherical.ToString("0.00"));
                sb.Append(',');
                sb.Append(g.OS_Cylindrical.ToString("0.00"));
                sb.Append(',');
                sb.Append(g.OS_Axis.ToString("000"));
                sb.Append(',');
                sb.Append(g.OS_Add.ToString("0.00"));
                sb.Append(',');
                sb.Append(g.OD_Spherical.ToString("0.00"));
                sb.Append(',');
                sb.Append(g.OD_Cylindrical.ToString("0.00"));
                sb.Append(',');
                sb.Append(g.OD_Axis.ToString("000"));
                sb.Append(',');
                sb.Append(g.OD_Add.ToString("0.00"));
                sb.AppendLine();
            }

            return File(Encoding.UTF8.GetBytes(sb.ToString()), "text/plain", "inventory.csv");
        }
    }
}
