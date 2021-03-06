package pixel;

import java.io.IOException;
import java.lang.reflect.Field;

import dataUtils.conversion.Convertable;
import dataUtils.conversion.InConverter;
import dataUtils.conversion.OutConverter;

/**
 * The universal data storage object for everything a pixel could need to store.
 * Just extend it and give it all the attributes it needs. But beware ! only
 * primitive types like <b>byte, int, char, boolean</b> and Classes with the Interface
 * {@link Convertable} are supported !
 * @author Quantum Hero
 *
 */
public abstract class UDS{
	
	public UDS(InConverter in){
		load(in);
	}
	
	public UDS() {
		
	}
	
	public void save(OutConverter out, boolean header) {
		try {
			if(header)out.writeShort((short) 0x7fff);
			Field[] f = this.getClass().getDeclaredFields();
			for (Field field : f) {
				if(byte.class == field.getType()){out.writeByte(field.getByte(this));continue;}
				if(short.class == field.getType()){out.writeShort(field.getShort(this));continue;}
				if(int.class == field.getType()){out.writeInt(field.getInt(this));continue;}
				if(long.class == field.getType()){out.writeLong(field.getLong(this));continue;}
				if(float.class == field.getType()){out.writeFloat(field.getFloat(this));continue;}
				if(double.class == field.getType()){out.writeDouble(field.getDouble(this));continue;}
				if(boolean.class == field.getType()){out.writeBoolean(field.getBoolean(this));continue;}
				if(char.class == field.getType()){out.writeChar(field.getChar(this));continue;}
				((Convertable)field.get(this)).save(out);
			}
		} catch (IllegalArgumentException | SecurityException | IllegalAccessException | IOException e) {
			e.printStackTrace();
		}
	}

	public UDS load(InConverter in) {
		try {
			Field[] f = this.getClass().getDeclaredFields();
			for (Field field : f) {
				if(byte.class == field.getType()){field.set(this,in.readByte());continue;}
				if(short.class == field.getType()){field.set(this,in.readShort());continue;}
				if(int.class == field.getType()){field.set(this,in.readInt());continue;}
				if(long.class == field.getType()){field.set(this,in.readLong());continue;}
				if(float.class == field.getType()){field.set(this,in.readFloat());continue;}
				if(double.class == field.getType()){field.set(this,in.readDouble());continue;}
				if(boolean.class == field.getType()){field.set(this,in.readBoolean());continue;}
				if(char.class == field.getType()){field.set(this,in.readChar());continue;}
				((Convertable)field.get(this)).load(in);
			}
		} catch (IllegalArgumentException | SecurityException | IllegalAccessException | IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
}
