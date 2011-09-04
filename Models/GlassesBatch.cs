using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ComponentModel.DataAnnotations;

namespace Vision.Models
{
    public class GlassesBatch
    {
        public int GlassesBatchID { get; set; }

        [StringLength(2000)]
        public string CallNumbers { get; set; }
    }
}