using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Vision.Models;
using System.Data.Entity;
using System.Text;

namespace Vision
{
    public static class DbSetGlassesExtensions
    {
        public static List<GlassesSearchResult> Scoring(this DbSet<Glasses> glassesDbSet, Glasses searchParameters)
        {
            var results = new List<GlassesSearchResult>();
            foreach (var glasses in glassesDbSet)
            {
                GlassesSearchResult result = new GlassesSearchResult { Glasses = glasses };
                result.MatchScore = 100f;
                StringBuilder details = new StringBuilder();

                /*** Calculate Cyl ***/
                float od_cyl_score = 2 * scoreCylindrical(searchParameters.OD_Cylindrical, glasses.OD_Cylindrical);
                result.MatchScore -= od_cyl_score;
                details.AppendFormat("OD Cyl: -{0}\n", od_cyl_score);

                float os_cyl_score = 2 * scoreCylindrical(searchParameters.OS_Cylindrical, glasses.OS_Cylindrical);
                result.MatchScore -= os_cyl_score;
                details.AppendFormat("OS Cyl: -{0}\n", os_cyl_score);

                // if the two eyes have an adjustment difference of more than 1 step, then penalize
                if (Math.Abs(od_cyl_score - os_cyl_score) > 2)
                {
                    result.MatchScore -= 5;
                    details.Append("OS OD cyl diff > 1 step: -5\n");
                }

                /*** Calculate Sph ***/
                float od_sph_score = 2 * scoreSpherical(searchParameters.OD_Spherical, glasses.OD_Spherical);
                result.MatchScore -= od_sph_score;
                details.AppendFormat("OD Sph: -{0}\n", od_sph_score);

                float os_sph_score = 2 * scoreSpherical(searchParameters.OS_Spherical, glasses.OS_Spherical);
                result.MatchScore -= os_sph_score;
                details.AppendFormat("OS Sph: -{0}\n", os_sph_score);

                // if the two eyes have an adjustment difference of more than 1 step, then penalize
                if (Math.Abs(od_sph_score - os_sph_score) > 2)
                {
                    result.MatchScore -= 5;
                    details.Append("OS OD sph diff > 1 step: -5\n");
                }

                // check if any of the properties are different more than one step
                float minStep = Math.Min(od_cyl_score, Math.Min(os_cyl_score, Math.Min(od_sph_score, os_sph_score)));
                if (od_cyl_score - minStep > 2
                    || os_cyl_score - minStep > 2
                    || od_sph_score - minStep > 2
                    || os_sph_score - minStep > 2)
                {
                    result.MatchScore -= 5;
                    details.Append("Cyl and/or sph diff > 1 step: -5\n");
                }

                // spherical equivalant (sph + cyl/2)
                if (od_sph_score > 0 && od_sph_score == od_cyl_score / 2)
                {
                    result.MatchScore += 7;
                    details.Append("OD Spherical equiv: +7\n");
                }
                if (os_sph_score > 0 && os_sph_score == os_cyl_score / 2)
                {
                    result.MatchScore += 7;
                    details.Append("OS Spherical equiv: +7\n");
                }

                /*** Calculate Axis ***/
                float od_axis_score = scoreAxis(searchParameters.OD_Cylindrical, searchParameters.OD_Axis, glasses.OD_Axis);
                result.MatchScore -= od_axis_score;
                details.AppendFormat("OD Axis: -{0}\n", od_axis_score);

                float os_axis_score = scoreAxis(searchParameters.OS_Cylindrical, searchParameters.OS_Axis, glasses.OS_Axis);
                result.MatchScore -= os_axis_score;
                details.AppendFormat("OS Axis: -{0}\n", os_axis_score);

                if (result.MatchScore >= 0)
                {
                    result.MatchScoreDetails = details.ToString();
                    results.Add(result);
                }
            }
            return results;
        }

        private static float scoreSpherical(float search, float glasses)
        {
            if (search >= 0 && search >= glasses && glasses >= 0)
            {
                return (search - glasses) / 0.25f;
            }
            else if (search < 0 && search <= glasses && glasses <= 0)
            {
                return (search - glasses) / -0.25f;
            }
            return 100f;
        }

        private static float scoreCylindrical(float search, float glasses)
        {
            if (search - 0.25f <= glasses)
            {
                return Math.Abs(search - glasses) / 0.25f;
            }
            return 100f;
        }

        private static float scoreAxis(float search_cyl, int search_axis, int glasses_axis)
        {
            int reasonableRange = 5;
            if (search_cyl >= -1.0f)
                reasonableRange = 20;
            else if (search_cyl >= -2.0f)
                reasonableRange = 15;
            else if (search_cyl >= -3.0f)
                reasonableRange = 10;

            int distance = Math.Abs(search_axis - glasses_axis);
            distance = Math.Min(distance, 180 - distance);

            if (distance < reasonableRange)
                return (8 * distance) / reasonableRange;
            else if (distance < reasonableRange * 2)
                return 15;
            else if (distance < reasonableRange * 3)
                return 25;
            else
                return 35;
        }
    }
}