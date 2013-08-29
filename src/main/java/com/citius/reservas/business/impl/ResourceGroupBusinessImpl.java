/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.citius.reservas.business.impl;

import com.citius.reservas.models.Resource;
import com.citius.reservas.models.ResourceGroup;
import com.citius.reservas.business.ResourceBusiness;
import com.citius.reservas.business.ResourceGroupBusiness;
import com.citius.reservas.repositories.ResourceGroupRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Esther
 */
@Service
public class ResourceGroupBusinessImpl implements ResourceGroupBusiness {

    @Autowired
    private ResourceGroupRepository resourceGroupRepository;
    
    @Autowired
    private ResourceBusiness resourceBusiness;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<ResourceGroup> readAll() {

        List<ResourceGroup> l = resourceGroupRepository.findAll();
        return l;
    }

    @Transactional
    @Override
    public ResourceGroup read(Integer id) {
        return resourceGroupRepository.find(id);
    }

    @Transactional
    @Override
    public ResourceGroup create(ResourceGroup r) {

        r = resourceGroupRepository.save(r);

        return r;
    }

    @Transactional
    @Override
    public ResourceGroup save(ResourceGroup resourceGroup) {

        resourceGroup = resourceGroupRepository.save(resourceGroup);

        return resourceGroup;
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        ResourceGroup g = resourceGroupRepository.find(id);
        if (g == null) {
            throw new IllegalArgumentException(id + " can't be found");
        } else {
            ResourceGroup def = resourceGroupRepository.findByName("default");
            for (Resource r : g.getResources()) {
                r.setGroup(def);
            }
        }
        resourceGroupRepository.delete(id);
    }

    @Transactional
    @Override
    public void deleteWithResources(Integer id) {
        ResourceGroup g = resourceGroupRepository.find(id);
        if (g == null) {
            throw new IllegalArgumentException(id + " can't be found");
        } else {
            for (Resource r : g.getResources()) {
                resourceBusiness.delete(r.getId());
            }
        }
        
        resourceGroupRepository.delete(id);
    }
}