using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Vision.Models
{
    public class PullRequest
    {
        public int PullRequestID { get; set; }
        public int Group { get; set; }
        public int Number { get; set; }
    }
}