package gfx;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import map.Map;

public class Screen {

	public static final int SHADOW_SCALE = 2;
	public static final int MAP_SCALE = 2;
	public static int MAP_ZOOM = 2;
	
	public static int RENDER_CHUNK_SIZE = 128;

	public static int xOffset = 0;
	public static int yOffset = 0;
	
	public static int width;
	public static int height;
	
	private static Shader default_shader;
	private static Shader colored_shader;
	private static Shader map_shader;
	
	private static Matrix4f projection;
	private static Model tileModel;
	
	public static void initialize(int width, int height) {
		Screen.width = width;
		Screen.height = height;
		projection = new Matrix4f().setOrtho2D(-width, width, -height, height);
		default_shader = new Shader("default_shader");
		colored_shader = new Shader("colored_shader");
		map_shader = new Shader("map_shader");
		tileModel = new Model();
	}
	
	public static int combineColors(int colorBack, int colorFront){
		float af = (colorFront>>24)&0xff;
		if(af != 0xff){
			int ab = (colorBack>>24)&0xff;
			if(ab != 0){
				int rf = (colorFront>>16)&0xff, gf = (colorFront>>8)&0xff, bf = colorFront&0xff;
				int rb,gb,bb;
				rb = (int)((af/255*rf) + ((255-af)/255*((colorBack>>16)&0xff)));
				gb = (int)((af/255*gf) + ((255-af)/255*((colorBack>> 8)&0xff)));
				bb = (int)((af/255*bf) + ((255-af)/255*((colorBack    )&0xff)));
				if(ab != 255){
					ab = (int) (af+(255-af)/255*((colorBack>>24)&0xff));
				}
				return (ab<<24)|(rb<<16)|(gb<<8)|bb;
			}else{
				return colorFront;
			}
		}else{
			return colorFront;
		}
	}
	
/*	private void drawPixel(int x, int y, int color, boolean gui){
		int width = this.width;
		int height = this.height;
		if(!gui) { width /= MAP_ZOOM; height /= MAP_ZOOM;}
		double a = (color>>24)&0xff;
		if(a != 0xff){
			int r = (color>>16)&0xff, g = (color>>8)&0xff, b = color&0xff;
			int col,ro,go,bo,ao;
			if(x >= 0 && x < width && y >= 0 && y < height){
				col = gui ? GUI[x + (y)*width] : pixels[x + (y)*width];
				ao = (col>>24)&0xff;
				if(ao != 0){
					ro = (int)((a/255*r) + ((255-a)/255*((col>>16)&0xff)));
					go = (int)((a/255*g) + ((255-a)/255*((col>> 8)&0xff)));
					bo = (int)((a/255*b) + ((255-a)/255*((col    )&0xff)));
					if(ao != 255){
						ao = (int) (a+(255-a)/255*((col>>24)&0xff));
					}
					if(gui) GUI[x + (y)*width] = (ao<<24)|(ro<<16)|(go<<8)|bo;
					else pixels[x + (y)*width] = (ao<<24)|(ro<<16)|(go<<8)|bo;
				}else{
					if(gui) GUI[x + (y)*width] = color;
					else pixels[x + (y)*width] = color;
				}
			}
		}else{
			if(x >= 0 && x < width && y >= 0 && y < height){
				if(gui) GUI[x + (y)*width] = color;
				else pixels[x + (y)*width] = color;
			}
		}
	}*/
	
	public static void drawMapSprite(int xPos, int yPos, SpriteSheet sheet, int tile, boolean mirrorX, boolean mirrorY, int color){
		drawSprite(xPos, yPos, sheet, tile, mirrorX, mirrorY, color, true);
	}
	
	public static void drawMapSprite(int xPos, int yPos, SpriteSheet sheet, int tile, boolean mirrorX, boolean mirrorY){
		drawSprite(xPos, yPos, sheet, tile, mirrorX, mirrorY, 0, true);
	}
	
	public static void drawMapSprite(int xPos, int yPos, SpriteSheet sheet, int tile){
		drawSprite(xPos, yPos, sheet, tile, false, false, 0, true);
	}
	
	public static void drawMapSprite(int xPos, int yPos, SpriteSheet sheet){
		drawSprite(xPos, yPos, sheet, 0, false, false, 0, true);
	}
	
	public static void drawGUISprite(int xPos, int yPos, SpriteSheet sheet, int tile, boolean mirrorX, boolean mirrorY, int color){
		drawSprite(xPos, yPos, sheet, tile, mirrorX, mirrorY, color, false);
	}
	
	public static void drawGUISprite(int xPos, int yPos, SpriteSheet sheet, int tile, boolean mirrorX, boolean mirrorY){
		drawSprite(xPos, yPos, sheet, tile, mirrorX, mirrorY, 0, false);
	}
	
	public static void drawGUISprite(int xPos, int yPos, SpriteSheet sheet, int tile){
		drawSprite(xPos, yPos, sheet, tile, false, false, 0, false);
	}
	
	public static void drawGUISprite(int xPos, int yPos, SpriteSheet sheet){
		drawSprite(xPos, yPos, sheet, 0, false, false, 0, false);
	}
	
	public static void drawSprite(float xPos, float yPos, SpriteSheet sheet, int tile, boolean mirrorX, boolean mirrorY, int color, boolean onMap){
		if(onMap) {
			xPos-=xOffset;xPos*=MAP_SCALE*MAP_ZOOM;
			yPos-=yOffset;yPos*=MAP_SCALE*MAP_ZOOM;
			xPos += sheet.getWidth()/2f*MAP_ZOOM;
			yPos += sheet.getHeight()/2f*MAP_ZOOM;
		}else {
			xPos += sheet.getWidth()/2f;
			yPos += sheet.getHeight()/2f;
		}
		yPos = height - yPos;
		
		glActiveTexture(GL_TEXTURE0 + 0);
		glBindTexture(GL_TEXTURE_2D, sheet.getID(tile));
		Matrix4f target = projection.mul(new Matrix4f().translate(new Vector3f(xPos*2-width, yPos*2-height, 0)), new Matrix4f());
		
		float ratio = (((float)sheet.getHeight())/((float)sheet.getWidth()));
		target.mul(new Matrix4f().ortho2D(ratio*(mirrorX ? 1.0f : -1.0f), ratio*(mirrorX ? -1.0f : 1.0f), (mirrorY ? 1.0f : -1.0f), (mirrorY ? -1.0f : 1.0f)));
		target.scale(sheet.getHeight()*(onMap ? MAP_ZOOM : 1));
		if(color!=0) {
			colored_shader.bind();
			colored_shader.setUniform("color",
					((color>>24)&0xff)/255.0f,
					((color>>16)&0xff)/255.0f,
					((color>>8 )&0xff)/255.0f,
					((color    )&0xff)/255.0f);
			colored_shader.setUniform("sampler", 0);
			colored_shader.setUniform("projection", target);
		}else {
			default_shader.bind();
			default_shader.setUniform("sampler", 0);
			default_shader.setUniform("projection", target);
		}
		tileModel.render();
	}
	
	public static void drawMap(Map map){
		int textures[] = new int[4];
		Matrix4f target = null;
		float X, Y;
		map_shader.bind();
		map_shader.setUniform("sampler", 0);
		glActiveTexture(GL_TEXTURE0 + 0);

//		int nx = 0, ny = 0;
//		float ox = 0, oy = 0;
		for (float x = -RENDER_CHUNK_SIZE/2; x < width/2/MAP_ZOOM+RENDER_CHUNK_SIZE; x+=RENDER_CHUNK_SIZE) {
//			ny=0;
			for (float y = -RENDER_CHUNK_SIZE/2; y < height/2/MAP_ZOOM+RENDER_CHUNK_SIZE; y+=RENDER_CHUNK_SIZE) {
				for (int l : Map.LAYER_ALL) {
					textures[l] = map.getRenderChunk((int)(x+xOffset), (int)(y+yOffset), l);
				}
//				if(target == null) {
					X = x;
					Y = y;
					X -= (X+xOffset)%RENDER_CHUNK_SIZE;
					Y -= (Y+yOffset)%RENDER_CHUNK_SIZE;
					Y+=RENDER_CHUNK_SIZE/2;
					X+=RENDER_CHUNK_SIZE/2;
					X*=MAP_SCALE*MAP_ZOOM;
					Y*=MAP_SCALE*MAP_ZOOM;
					Y = height - Y;
					target = projection.mul(new Matrix4f().translate(new Vector3f(X*2-width, Y*2-height, 0)), new Matrix4f());
					target.scale(RENDER_CHUNK_SIZE*MAP_SCALE*MAP_ZOOM);
//					ox = target.m30();
//					oy = target.m31();
//				}
				
				map_shader.setUniform("projection", target);
				for (int l : Map.LAYER_ALL) {
					if(textures[l] == 0)continue;
					map_shader.setUniform("layer", l);
					glBindTexture(GL_TEXTURE_2D, textures[l]);
					tileModel.render();
				}
//				Game.ccFont.render((int)(x*2), (int)(y*2), nx+" "+ny, 0, 0xffff0000);
//				ny++;
//				target.m30(ox+target.m00()*2*nx);
//				target.m31(oy-target.m11()*2*ny);
			}
//			nx++;
		}
//		System.out.println(nx+" "+ny);
	}
}
