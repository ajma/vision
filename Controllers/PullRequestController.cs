using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Vision.Models;

namespace Vision.Controllers
{
    public class PullRequestController : Controller
    {
        VisionContext context = new VisionContext();

        public ActionResult Index()
        {
            var requests = new List<PullRequest>();
            requests = context.PullRequests.OrderByDescending(r => r.PullRequestID).ToList<PullRequest>();
            return View(requests);
        }

        public ActionResult PullRequest(int group, int number)
        {
            context.PullRequests.Add(new PullRequest { Group = group, Number = number });
            context.SaveChanges();
            return Json(true, JsonRequestBehavior.AllowGet);
        }

        public ActionResult Remove(int pullRequestID)
        {
            var request = context.PullRequests.Find(pullRequestID);
            context.PullRequests.Remove(request);
            context.SaveChanges();
            return Json(true);
        }
    }
}
