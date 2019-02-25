package junit;

import static org.junit.Assert.*;
import org.junit.Test;
import tools.WikiParser_en;

public class TestWikiParser {
	
	private WikiParser_en wp;

	
	public TestWikiParser() {
		wp = new WikiParser_en();
	}
	
	
	@Test
	public void wpShouldExtractContentByPageId() {
		String content = wp.getExtractById("16802423");
		
		assertTrue(content.contains("Objectors may call this a scandal, but the building is sure to become the third 'somewhat strange' manmade object in the Augarten, "
				+ "the others being the two battle towers built during the Second World War."));

	}

}
