package it.unimib.disco.lta.timedKTail.ui;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ValidateTracesTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMain() throws IOException {
		String[] args = new String[]{"ValidateTracesTest.TA.jtml","storageTest/ValidateTracesTest/inference"};
		InferModel.main(args);
		
		args = new String[]{"ValidateTracesTest.TA.jtml","storageTest/ValidateTracesTest/validation"};
		ValidateTraces.main(args);
		
	}

}
