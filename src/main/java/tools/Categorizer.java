package tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.PlainTextByLineStream;

public class Categorizer {

	private InputStream dataIn;
	private DocumentCategorizerME categorizer;

	private static final Logger log = Logger.getLogger(Categorizer.class.getName());

	// private List<String> categories_en = Arrays.asList("Architecture", "History",
	// "Religion", "Politics", "Sports", "Geography");
	// private List<String> categories_de = Arrays.asList("Architektur",
	// "Geschichte", "Religion", "Politik", "Sport", "Geographie");

	public Categorizer(String language) {
		String categorizerModel = getCategorizerModel(language);
		try {
			dataIn = new FileInputStream(categorizerModel);
			DocumentSampleStream docstream = new DocumentSampleStream(new PlainTextByLineStream(dataIn, "UTF-8"));
			DoccatModel model = DocumentCategorizerME.train("en", docstream);
			categorizer = new DocumentCategorizerME(model);
		} catch (IOException e) {
			log.severe(e.toString());
		} finally {
			if (dataIn != null) {
				try {
					dataIn.close();
				} catch (IOException e) {
					log.severe(e.toString());
				}
			}
		}
	}

	private String getCategorizerModel(String language) {
		if (language.equals("en")) {
			return "categorizer.model.en";
		}

		return "categorizer.model.de";
	}

	public String categorizeSections(String content, String language) {
		content = Jsoup.parse(content).text();

		double[] outcome = categorizer.categorize(content);

		if (language.equals("en")) {
			if (StringUtils.countMatches(categorizer.getAllResults(outcome).toString(), "0,25") == 4)
				return "";
		} else {
			if (StringUtils.countMatches(categorizer.getAllResults(outcome).toString(), "0,20") == 4)
				return "";
		}

		return categorizer.getBestCategory(outcome);

	}

}