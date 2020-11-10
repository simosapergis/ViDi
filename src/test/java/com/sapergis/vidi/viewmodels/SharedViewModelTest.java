package com.sapergis.vidi.viewmodels;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Assert;

import static org.junit.Assert.*;

public class SharedViewModelTest extends TestCase {
    SharedViewModel sharedViewModel;

    @Before
    public void setUp() throws Exception {
        sharedViewModel = new SharedViewModel();
    }

    public void testGetCamera(){
        Assert.assertTrue(sharedViewModel.startCamera instanceof Runnable);
    }

}