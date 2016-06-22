package com.googlecode.paradox.utils.filefilters;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 *
 * @author 72330554168
 */
public class PrimaryKeyFilterTest {

	private final PrimaryKeyFilter filter = new PrimaryKeyFilter();

	@Test
	public void testAccept() {
		final File file = new File("teste.px");
		assertTrue(filter.accept(file));
	}
}
