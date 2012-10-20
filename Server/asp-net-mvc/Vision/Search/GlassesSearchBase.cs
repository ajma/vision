using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Vision.Models;
using System.Text;

namespace Vision.Search
{
    public abstract class GlassesSearchBase
    {
        public IEnumerable<GlassesSearchResult> Search(IEnumerable<Glasses> inventory, Glasses query)
        {
            var results = new List<GlassesSearchResult>(inventory.Count());
            foreach (Glasses glasses in inventory)
            {
                GlassesSearchResult result = new GlassesSearchResult { Glasses = glasses };
                result.Score = 100f;
                StringBuilder details = new StringBuilder();

                result.Score -= getSphericalScore(query, glasses, details);
                result.Score -= getCylinderScore(query, glasses, details);
                result.Score -= getAxisScore(query, glasses, details);
                result.Score -= getAddScore(query, glasses, details);

                result.Score -= getSunglassesScore(query, glasses, details);
                result.Score -= getGenderScore(query, glasses, details);
                result.Score -= getSizeScore(query, glasses, details);

                if (result.Score < 0f)
                    result.Score = 0f;

                result.ScoreDetails = details.ToString();

                insertResultIntoSortedLocation(results, result);
            }
            return results;
        }

        private void insertResultIntoSortedLocation(List<GlassesSearchResult> results, GlassesSearchResult result)
        {
            for (int i = 0; i < results.Count(); i++)
            {
                if (result.Score > results[i].Score)
                {
                    results.Insert(i, result);
                    return;
                }
            }
            results.Add(result);
        }

        protected abstract float getSphericalScore(Glasses query, Glasses glasses, StringBuilder details);
        protected abstract float getCylinderScore(Glasses query, Glasses glasses, StringBuilder details);
        protected abstract float getAxisScore(Glasses query, Glasses glasses, StringBuilder details);
        protected abstract float getAddScore(Glasses query, Glasses glasses, StringBuilder details);

        protected abstract float getSunglassesScore(Glasses query, Glasses glasses, StringBuilder details);
        protected abstract float getGenderScore(Glasses query, Glasses glasses, StringBuilder details);
        protected abstract float getSizeScore(Glasses query, Glasses glasses, StringBuilder details);

        #region Helpers
        /// <summary>
        /// Figures out the number of quarter steps difference.
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <returns></returns>
        public static int quarterStepsDifference(float a, float b)
        {
            // if there's one is + and the other is -, then we should never match
            if ((a > 0 && b < 0) || (a < 0 && b > 0))
                return -100;

            if (b > 0)
            {
                return (int)((b - a) / 0.25f);
            }
            else
            {
                return (int)((a - b) / 0.25f);
            }
        }
        #endregion
    }
}