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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author pblumenk
 */
public class MGSReader {

    private MGSEntry nextElement = null;
    private boolean eof = false;

    private MGXMasterI master;
    private JobI job;
    private Map<String, AttributeTypeI> attrTypeCache = new HashMap<>();
    private Map<AttributeTypeI, Collection<AttributeI>> attrCache = new HashMap<>();

    private BufferedReader input;

    private String nextHeader;
    private long line = 1;

    public MGSReader(String filePath, MGXMasterI master, JobI job) throws FileNotFoundException, MGXException {
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
            return;
        }
        initAttributeTypeCache();
    }

    public boolean hasNext() throws MGXException, IOException {
        if (nextElement != null) {
            return true;
        }
        if (eof) {
            return false;
        }

        nextElement = new MGSEntry(nextHeader);
        int lastHDNumber = -1;
        AttributeI lastHDAttribute = null;

        try {
            String nextLine;
            //Parse file to next READ line -> all attributes for one sequence
            while (!(nextLine = input.readLine()).startsWith("READ")) {
                // e.g. "AT      NCBI_ROOT       Root    0       250     HD      1"
                String[] splittedLine = nextLine.split("\t");
                if (splittedLine.length < 5) {
                    throw new IOException(String.format("Line %d has fewer than 5 columns", line));
                }
                char valueType;
                switch (splittedLine[5].charAt(1)) {
                    case 'N':
                        valueType = AttributeTypeI.VALUE_NUMERIC;
                        break;
                    case 'D':
                        valueType = AttributeTypeI.VALUE_DISCRETE;
                        break;
                    default:
                        throw new IOException(String.format("Unknown attribute type value type in line %d", line));
                }

                int start = Integer.parseInt(splittedLine[3]);
                int stop = Integer.parseInt(splittedLine[4]);

                switch (splittedLine[5].charAt(0)) {
                    case 'B': {
                        AttributeTypeI attrType = getAttributeType(splittedLine[1], valueType, AttributeTypeI.STRUCTURE_BASIC);
                        AttributeI attr = createBasicAttribute(attrType, splittedLine[2]);
                        nextElement.add(attr, start, stop);
                        break;
                    }
                    case 'H': {
                        if (splittedLine.length != 7) {
                            throw new IOException(String.format("Missing column in line %d", line));
                        }
                        int currentHDNumber = Integer.parseInt(splittedLine[6]);
                        if (lastHDNumber != currentHDNumber) {
                            lastHDNumber = currentHDNumber;
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
        return createHierarchicalAttribute(attrType, attrValue, null);
    }

    private AttributeI createHierarchicalAttribute(AttributeTypeI attrType, String attrValue, AttributeI parent) throws MGXException {
        AttributeI attr;
        if (!attrCache.containsKey(attrType)) {
            attrCache.put(attrType, new LinkedList<AttributeI>());
        } else {
            //check if attribute is in cache
            attr = getAttribute(attrType, attrValue);
            if (attr != null) {
                return attr;
            }
        }

        attr = master.Attribute().create(job, attrType, attrValue, parent);
        attrCache.get(attrType).add(attr);
        return attr;
    }

    private AttributeTypeI getAttributeType(String name, char valueType, char structure) throws MGXException {
        if (attrTypeCache.containsKey(name)) {
            return attrTypeCache.get(name);
        }

        AttributeTypeI newAT = master.AttributeType().create(name, valueType, structure);
        attrTypeCache.put(name, newAT);
        attrCache.put(newAT, new LinkedList<AttributeI>());
        return newAT;
    }

    private void initAttributeTypeCache() throws MGXException {
        Iterator<AttributeTypeI> it = master.AttributeType().fetchall();
        while (it.hasNext()) {
            AttributeTypeI at = it.next();
            attrTypeCache.put(at.getName(), at);
        }
    }

    private AttributeI getAttribute(AttributeTypeI at, String value) {
        Collection<AttributeI> attrCol = attrCache.get(at);
        if (attrCol == null) {
            return null;
        }

        for (AttributeI attr : attrCol) {
            if (attr.getValue().equals(value)) {
                return attr;
            }
        }

        return null;
    }
}
