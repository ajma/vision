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
        public static List<GlassesSearchResult> CallNumberSearch(this DbSet<Glasses> glassesDbSet, int group, int number)
        {
            return glassesDbSet.Where(g => g.Group == group && g.Number == number).Select(g => new GlassesSearchResult {
                MatchScore = 100,
                Glasses = g
            }).ToList();
        }

        public static List<GlassesSearchResult> FuzzySearch(this DbSet<Glasses> glassesDbSet, Glasses searchParameters)
        {
            var searchService = new Vision.Services.MikeTamSearch();
            var results = new List<GlassesSearchResult>();
            foreach (var glasses in glassesDbSet)
            {
                var result = searchService.ScoreGlasses(searchParameters, glasses);

                if (result.MatchScore >= 0)
                {
                    results.Add(result);
                }
            }
            return results;
        }

    }
}