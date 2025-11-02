package com.sg.floormaster.dao;

import com.sg.floormaster.model.Tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class FlooringMasteryTaxDaoStubImpl implements FlooringMasteryTaxDao {

    private Tax onlyTax;

    public FlooringMasteryTaxDaoStubImpl() {
        onlyTax = new Tax("Texas", "TX",
                new BigDecimal("4.45").setScale(2, RoundingMode.HALF_UP));
    }

    public FlooringMasteryTaxDaoStubImpl(Tax tax) {
        onlyTax = tax;
    }


    @Override
    public List<Tax> getAllTaxes() {
        List<Tax> taxes = new ArrayList<>();
        taxes.add(onlyTax);
        return taxes;
    }
}
