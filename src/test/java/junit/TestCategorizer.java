package junit;


import static org.junit.Assert.assertEquals;
import org.junit.Test;
import tools.Categorizer;



public class TestCategorizer {

	private Categorizer cat_en;
	
	
	public TestCategorizer() {
		cat_en = new Categorizer("en");
	}
	
	
	@Test
	public void categorizerShouldrecognizeHistorySections() {
		String content1 = "The street was built to replace the city walls, which had been built during the 13th century and "
				+ "funded by the ransom payment derived from the release of Richard I of England, and reinforced as a consequence "
				+ "of the First Turkish Siege in 1529. The walls were surrounded by a glacis about 500m wide, where buildings "
				+ "and vegetation were prohibited. But by the late 18th century these fortifications had become obsolete. Under "
				+ "Emperor Joseph II, streets and walkways were built in the glacis, lit by lanterns and lined by trees. "
				+ "Craftsmen built open-air workshops, and stalls were set up. But the Revolution of 1848 was required to "
				+ "trigger a more significant change. In 1850, the Vorst‰dte (today the Districts II to IX) were incorporated "
				+ "into the municipality, which made the city walls an impediment to traffic. In 1857, Emperor Franz Joseph I "
				+ "of Austria issued the decree \"It is My will\" (Es ist Mein Wille at Wikisource) ordering the demolition "
				+ "of the city walls and moats. In his decree, he laid out the exact size of the boulevard, as well as the "
				+ "geographical positions and functions of the new buildings. The Ringstraﬂe and the planned buildings were "
				+ "intended to be a showcase for the grandeur and glory of the Habsburg Empire.";
		
		
		
		String content2 = "The Gasometers were built from 1896 to 1899 in the Simmering district of Vienna near the Gaswerk Simmering gas works of the district. "
				+ "The containers were used to help supply Vienna with town gas, facilities which had previously been provided by the English firm Inter Continental "
				+ "Gas Association (ICGA). Once the contracts with the ICGA expired, the city decided to construct facilities to handle its own gas needs. At the time,"
				+ " the design was the largest in all of Europe. The Gasometers were retired in 1984 due to new technologies in gasometer construction, as well as the "
				+ "city's conversion from town gas and coal gas to natural gas. Gas can be stored underground or in modern high-pressure gas storage spheres under much "
				+ "higher pressures and in smaller volumes than the relatively large gasometers. In 1978, they were designated as protected historic landmarks. Vienna undertook "
				+ "a remodelling and revitalization of the protected monuments and in 1995 called for ideas for the new use of the structures. The chosen designs by the "
				+ "architects Jean Nouvel (Gasometer A), Coop Himmelblau (Gasometer B), Manfred Wehdorn (Gasometer C) and Wilhelm Holzbauer (Gasometer D) were completed "
				+ "between 1999 and 2001. Each gasometer was divided into several zones for living (apartments in the top), working (offices in the middle floors) and "
				+ "entertainment and shopping (shopping malls in the ground floors). The shopping mall levels in each gasometer are connected to the others by skybridges. "
				+ "The historic exterior wall was conserved. One of the ideas rejected for the project was the plan by architect Manfred Wehdorn to use the Gasometers for "
				+ "hotels and facilities for the planned World Expo in Vienna and Budapest. On 30 October 2001, the mayor attended the official grand opening of the "
				+ "Gasometers, but people had begun moving in as early as May 2001.";
		
		assertEquals("History", cat_en.categorizeSections(content1, "en"));
		
		assertEquals("History", cat_en.categorizeSections(content2, "en"));
		
	}
	
	@Test
	public void categorizerShouldrecognizeGeographySections() {
		String content1 = "The cemetery lies in the south of Dˆbling on the border to W‰hring in the Katastralgemeinde of Oberdˆbling, "
				+ "in the Hart‰ckerstraﬂe. The cemeteryís limits are defined in the south by the Peter-Jordan-Straﬂe, in the west by the "
				+ "Borkowskigasse and in the north by the Hart‰ckergasse. It thus covers an area of 49,981 m2 and provides space for 6853 plots.";
		
		String content2 = "K‰rntner Straﬂe (English: Carinthian Street) is the most famous shopping street in central Vienna . It runs from the Stephansplatz out to the Wiener Staatsoper at "
				+ "Karlsplatz on the Ringstraﬂe. The first record of K‰rntner Straﬂe is from 1257, as Strata Carintianorum, which refers to its importance as a trade route to the southern "
				+ "province of Carinthia.";
		
		String content3 = "Austria is a small, predominantly mountainous country in Central Europe, approx. between Germany, Italy and Hungary. It has a total area of 83,859 km≤, about twice "
				+ "the size of Switzerland and slightly smaller than the state of Maine. The landlocked country shares national borders with Switzerland (164 km) and the principality of "
				+ "Liechtenstein (35 km) to the west, Germany (784 km) and the Czech Republic (362 km) and Slovakia (91 km) to the north, Hungary to the east (346 km), and Slovenia (311 km) "
				+ "and Italy (430 km) to the south (total: 2563 km). The westernmost third of the somewhat pear-shaped country consists of a narrow corridor between Germany and Italy that is "
				+ "between thirty-two and sixty km wide. The rest of Austria lies to the east and has a maximum northñsouth width of 280 km. The country measures almost 600 km in length, extending "
				+ "from Lake Constance (German Bodensee) on the Austrian-Swiss-German border in the west to the Neusiedler See on the Austrian-Hungarian border in the east. The contrast between "
				+ "these two lakes ñ one in the Alps and the other a typical steppe lake on the westernmost fringe of the Hungarian Plain ñ illustrates the diversity of Austria's landscape.";
		
		assertEquals("Geography", cat_en.categorizeSections(content1, "en"));	
		assertEquals("Geography", cat_en.categorizeSections(content2, "en"));
		assertEquals("Geography", cat_en.categorizeSections(content3, "en"));	
	}
	
}
