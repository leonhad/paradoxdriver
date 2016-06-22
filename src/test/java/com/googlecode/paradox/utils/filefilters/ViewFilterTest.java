package com.googlecode.paradox.utils.filefilters;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 *
 * @author 72330554168
 */
public class ViewFilterTest {

	private final ViewFilter filter = new ViewFilter();

	@Test
	public void testAccept() {
		final File file = new File("teste.qbe");
		assertTrue(filter.accept(file));
	}

}