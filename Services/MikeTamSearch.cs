using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Vision.Models;

namespace Vision.Services
{
    public class MikeTamSearch : GlassesSearchBase
    {
        protected override float getSphericalScore(GlassesQuery query, Models.Glasses glasses, System.Text.StringBuilder details)
        {
            float od_sph_score = query.OD_Blind ? 0 : 2 * scoreSpherical(query.OD_Spherical, glasses.OD_Spherical); 
            details.AppendFormat("OD Sph: -{0}\n", od_sph_score);

            float os_sph_score = query.OS_Blind ? 0 : 2 * scoreSpherical(query.OS_Spherical, glasses.OS_Spherical);
            details.AppendFormat("OS Sph: -{0}\n", os_sph_score);

            // if the two eyes have an adjustment difference of more than 1 step, then penalize
            float extraPenalty = 0f;
            if (!query.OD_Blind && !query.OS_Blind && Math.Abs(od_sph_score - os_sph_score) > 2)
            {
                extraPenalty += 5;
                details.Append("OS OD sph diff > 1 step: -5\n");
            }

            return od_sph_score + os_sph_score + extraPenalty;
        }

        protected override float getCylinderScore(GlassesQuery query, Models.Glasses glasses, System.Text.StringBuilder details)
        {
            float od_cyl_score = query.OD_Blind ? 0 : 2 * scoreCylindrical(query.OD_Cylindrical, glasses.OD_Cylindrical);
            details.AppendFormat("OD Cyl: -{0}\n", od_cyl_score);

            float os_cyl_score = query.OS_Blind ? 0 : 2 * scoreCylindrical(query.OS_Cylindrical, glasses.OS_Cylindrical);
            details.AppendFormat("OS Cyl: -{0}\n", os_cyl_score);

            // if the two eyes have an adjustment difference of more than 1 step, then penalize
            float extraPenalty = 0f;
            if (!query.OD_Blind && !query.OS_Blind && Math.Abs(od_cyl_score - os_cyl_score) > 2)
            {
                extraPenalty = 5f;
                details.Append("OS OD cyl diff > 1 step: -5\n");
            }

            return od_cyl_score + os_cyl_score + extraPenalty;
        }

        protected override float getAxisScore(GlassesQuery query, Models.Glasses glasses, System.Text.StringBuilder details)
        {
            float od_axis_score = query.OD_Blind ? 0 : scoreAxis(query.OD_Cylindrical, query.OD_Axis, glasses.OD_Axis);
            details.AppendFormat("OD Axis: -{0}\n", od_axis_score);

            float os_axis_score = query.OS_Blind ? 0 : scoreAxis(query.OS_Cylindrical, query.OS_Axis, glasses.OS_Axis);
            details.AppendFormat("OS Axis: -{0}\n", os_axis_score);

            return od_axis_score + os_axis_score;
        }

        protected override float getAddScore(GlassesQuery query, Models.Glasses glasses, System.Text.StringBuilder details)
        {
            return 0f;
        }

        protected override float getSunglassesScore(GlassesQuery query, Models.Glasses glasses, System.Text.StringBuilder details)
        {
            return 0f;
        }

        protected override float getGenderScore(GlassesQuery query, Models.Glasses glasses, System.Text.StringBuilder details)
        {
            return 0f;
        }

        protected override float getSizeScore(GlassesQuery query, Models.Glasses glasses, System.Text.StringBuilder details)
        {
            return 0f;
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