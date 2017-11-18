package de.mirkoruether.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.JAXB;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Serializer
{
    public static Map<String, Object> deserializeZip(File zip)
    {
        try
        {
            HashMap<String, Object> result = new HashMap<>();
            try(FileInputStream fin = new FileInputStream(zip);
                ZipInputStream zipin = new ZipInputStream(fin))
            {
                ZipEntry e = zipin.getNextEntry();
                try(ObjectInputStream objStream = new ObjectInputStream(zipin))
                {
                    while(e != null)
                    {
                        String name = e.getName();
                        Object obj = objStream.readObject();
                        result.put(name, obj);
                        e = zipin.getNextEntry();
                    }
                }
            }
            return result;
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Failed to serialize Objects"
                                       + " to zip-file "
                                       + zip == null ? "null" : zip.getPath(), ex);
        }
    }

    public static void serializeToZip(File zip, Serializable[] objs, String[] names)
    {
        if(names != null && objs.length != names.length)
        {
            throw new IllegalArgumentException("Objects and names differ in length");
        }

        try
        {
            try(FileOutputStream fout = new FileOutputStream(zip);
                ZipOutputStream zipout = new ZipOutputStream(fout))
            {
                ZipEntry e0 = new ZipEntry(names == null ? String.valueOf(0) : names[0]);
                zipout.putNextEntry(e0);

                try(ObjectOutputStream objStream = new ObjectOutputStream(zipout))
                {
                    objStream.writeObject(objs[0]);
                    for(int i = 1; i < objs.length; i++)
                    {
                        ZipEntry e = new ZipEntry(names == null ? String.valueOf(i) : names[i]);
                        zipout.putNextEntry(e);
                        objStream.writeObject(objs[i]);
                    }
                }
            }
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Failed to serialize Objects"
                                       + " to zip-file "
                                       + (zip == null ? "null" : zip.getPath()), ex);
        }
    }

    public static void serializeToFile(File f, Serializable obj)
    {
        try
        {
            try(FileOutputStream out = new FileOutputStream(f);
                ObjectOutputStream objStream = new ObjectOutputStream(out);)
            {
                objStream.writeObject(obj);
                out.flush();
            }
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Failed to serialize Object of type "
                                       + (obj == null ? "null" : obj.getClass().getName())
                                       + " to file " + f == null ? "null" : f.getPath(), ex);
        }
    }

    public static <T> T deserializeFile(File f, Class<T> clazz)
    {
        try
        {
            try(FileInputStream in = new FileInputStream(f);
                ObjectInputStream objStream = new ObjectInputStream(in);)
            {
                Object o = objStream.readObject();
                return clazz.cast(o);
            }
        }
        catch(ClassCastException ex)
        {
            throw new RuntimeException("The serialized Object is of wrong type", ex);
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Failed to deserialize Object of type "
                                       + (clazz == null ? "null" : clazz.getName())
                                       + " from file " + f == null ? "null" : f.getPath(), ex);
        }
    }

    public static String serializeAsString(Serializable obj)
    {
        return new String(serialize(obj), UTF_8);
    }

    public static byte[] serialize(Serializable obj)
    {
        try
        {
            try(ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream objStream = new ObjectOutputStream(out);)
            {
                objStream.writeObject(obj);
                return out.toByteArray();
            }
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Failed to serialize Object of type "
                                       + (obj == null ? "null" : obj.getClass().getName()), ex);
        }
    }

    public static <T> T deserialize(String s, Class<T> clazz)
    {
        return deserialize(s.getBytes(UTF_8), clazz);
    }

    public static <T> T deserialize(byte[] buf, Class<T> clazz)
    {
        try
        {
            try(ByteArrayInputStream in = new ByteArrayInputStream(buf);
                ObjectInputStream objStream = new ObjectInputStream(in);)
            {
                Object o = objStream.readObject();
                return clazz.cast(o);
            }
        }
        catch(ClassCastException ex)
        {
            throw new RuntimeException("The serialized Object is of wrong type", ex);
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Failed to deserialize Object of type "
                                       + (clazz == null ? "null" : clazz.getName()), ex);
        }
    }

    public static String toXml(Object jaxbObject)
    {
        StringWriter sw = new StringWriter();
        JAXB.marshal(jaxbObject, sw);
        return sw.toString();
    }

    public static <T> T fromXml(String xml, Class<T> clazz)
    {
        return JAXB.unmarshal(xml, clazz);
    }

    private Serializer()
    {
    }
}
