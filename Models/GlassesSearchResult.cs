
namespace Vision.Models
{
	public class GlassesSearchResult
	{
		public float MatchScore { get; set; }
        public string MatchScoreDetails { get; set; }
		public Glasses Glasses { get; set; }
	}
}