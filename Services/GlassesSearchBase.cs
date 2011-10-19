using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Vision.Models;
using System.Text;

namespace Vision.Services
{
    public abstract class GlassesSearchBase
    {
        public GlassesSearchResult ScoreGlasses(GlassesQuery query, Glasses glasses)
        {
            GlassesSearchResult result = new GlassesSearchResult { Glasses = glasses };
            result.MatchScore = 100f;
            StringBuilder details = new StringBuilder();

            result.MatchScore -= getSphericalScore(query, glasses, details);
            result.MatchScore -= getCylinderScore(query, glasses, details);
            result.MatchScore -= getAxisScore(query, glasses, details);
            result.MatchScore -= getAddScore(query, glasses, details);

            result.MatchScore -= getSunglassesScore(query, glasses, details);
            result.MatchScore -= getGenderScore(query, glasses, details);
            result.MatchScore -= getSizeScore(query, glasses, details);

            if (result.MatchScore < 0f)
                result.MatchScore = 0f;

            result.MatchScoreDetails = details.ToString();

            return result;
        }

        protected abstract float getSphericalScore(GlassesQuery query, Glasses glasses, StringBuilder details);
        protected abstract float getCylinderScore(GlassesQuery query, Glasses glasses, StringBuilder details);
        protected abstract float getAxisScore(GlassesQuery query, Glasses glasses, StringBuilder details);
        protected abstract float getAddScore(GlassesQuery query, Glasses glasses, StringBuilder details);

        protected abstract float getSunglassesScore(GlassesQuery query, Glasses glasses, StringBuilder details);
        protected abstract float getGenderScore(GlassesQuery query, Glasses glasses, StringBuilder details);
        protected abstract float getSizeScore(GlassesQuery query, Glasses glasses, StringBuilder details);

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