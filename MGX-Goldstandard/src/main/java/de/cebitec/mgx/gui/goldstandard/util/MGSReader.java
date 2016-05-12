package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author pblumenk
 */
public class MGSReader{

    MGSEntry nextElement = null;
    boolean eof = false;
    
    MGXMasterI master;
    JobI job;
    Map<String, AttributeTypeI> attrTypeCache = new HashMap<>();
    Map<AttributeTypeI, Collection<AttributeI>> attrCache = new HashMap<>();
    
    BufferedReader input;
    
    String nextHeader;
    long line = 1;

    public MGSReader(String filePath, MGXMasterI master, JobI job) throws FileNotFoundException {
        this.master = master;
        this.job = job;
        input = new BufferedReader(new FileReader(filePath));        
        try {
            String nextLine;
            while (!(nextLine = input.readLine()).startsWith("READ\t")) {
                line++;
            }
            nextHeader = nextLine.substring(5); // READ\t == 5 chars
        } catch (IOException | NullPointerException ex) {
            eof = true;
        }        
    }    
    
    
    public boolean hasNext() throws MGXException, IOException {
        if (nextElement != null)
            return true;
        if (eof)
            return false;
        
        nextElement = new MGSEntry(nextHeader);
        int lastHDNumber = -1;
        AttributeI lastHDAttribute = null;
        
        try {
            String nextLine;
            while (!(nextLine = input.readLine()).startsWith("READ")) {
                // e.g. "AT      NCBI_ROOT       Root    0       250     HD      1"
                String[] splittedLine = nextLine.split("\t");
                char valueType;
                if (splittedLine[5].charAt(1) != 'N' && splittedLine[5].charAt(1) != 'D'){
                    throw new IOException(String.format("Unknown attribute type value type in line %d", line));
                } else {
                    valueType = (splittedLine[5].charAt(1) == 'N') ? AttributeTypeI.VALUE_NUMERIC : AttributeTypeI.VALUE_DISCRETE;
                }                    
                int start = Integer.parseInt(splittedLine[3]);
                int stop = Integer.parseInt(splittedLine[4]);

                switch (splittedLine[5].charAt(0)) {
                    case 'B':
                        {
                            AttributeTypeI attrType = getAttributeType(splittedLine[1], valueType, AttributeTypeI.STRUCTURE_BASIC);
                            AttributeI attr = createBasicAttribute(attrType, splittedLine[2]);
                            nextElement.add(attr, start, stop);
                            break;
                        }
                    case 'H':
                        {
                            if (splittedLine.length != 7)
                                throw new IOException(String.format("Missing column in line %d", line));
                            if (lastHDNumber != Integer.parseInt(splittedLine[6])){
                                lastHDNumber = Integer.parseInt(splittedLine[6]);
                                lastHDAttribute = null;
                            }       
                            AttributeTypeI attrType = getAttributeType(splittedLine[1], valueType, AttributeTypeI.STRUCTURE_HIERARCHICAL);
                            lastHDAttribute = createHierarchicalAttribute(attrType, splittedLine[2], lastHDAttribute);
                            nextElement.add(lastHDAttribute, start, stop);
                            break;
                        }
                    default:
                        throw new IOException(String.format("Unknown attribute type structure in line %d", line));
                }
                line++;
            }
            nextHeader = nextLine.substring(5); // READ\t == 5 chars
        } catch (NullPointerException ex) {
            eof = true;
        }
        
        return true;
    }

    public MGSEntry next() {
        MGSEntry element = nextElement;
        nextElement = null;
        return element;
    }    

    private AttributeI createBasicAttribute(AttributeTypeI attrType, String attrValue) throws MGXException {
        AttributeI attr = getAttribute(attrType, attrValue);
        if (attr != null)
            return attr;
        
        attr = master.Attribute().create(job, attrType, attrValue, null);
        if (!attrCache.containsKey(attrType))
            attrCache.put(attrType, new HashSet<AttributeI>());
        attrCache.get(attrType).add(attr);
        return attr;
    }
    
    private AttributeI createHierarchicalAttribute(AttributeTypeI attrType, String attrValue, AttributeI parent) throws MGXException {
        AttributeI attr = getAttribute(attrType, attrValue);
        if (attr != null)
            return attr;
        
        attr = master.Attribute().create(job, attrType, attrValue, parent);
        if (!attrCache.containsKey(attrType))
            attrCache.put(attrType, new HashSet<AttributeI>());
        attrCache.get(attrType).add(attr);
        return attr;
    }
    
    private AttributeTypeI getAttributeType(String name, char valueType, char structure) throws MGXException{
        if (attrTypeCache.containsKey(name)){
            return attrTypeCache.get(name);
        } else {
            Iterator<AttributeTypeI> it = master.AttributeType().fetchall();
            while (it.hasNext()){
                AttributeTypeI at = it.next();
                if (at.getName().equals(name) && at.getStructure() == structure && at.getValueType() == valueType){
                    attrTypeCache.put(name, at);
                    return at;
                }
            }
        }
            
        AttributeTypeI newAT = master.AttributeType().create(name, valueType, structure);
        attrTypeCache.put(name, newAT);
        return newAT;
    }
    
    private AttributeI getAttribute(AttributeTypeI at, String value){
        Collection<AttributeI> attrCol;
        if (attrCache.containsKey(at))
            attrCol = attrCache.get(at);
        else
            return null;
        
        for (AttributeI attr : attrCol){
            if (attr.getValue().equals(value))
                return attr;
        }
        
        return null;
    }
}
