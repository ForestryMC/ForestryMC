package forestry.core.render;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.vecmath.Vector3f;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IModelRenderer;
import forestry.api.core.sprite.ISprite;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IColoredBakedQuad;

public class ModelRenderer implements IModelRenderer
{
	private static ModelRenderer instance;
	
	public static ModelRenderer getInstance()
	{
		if(instance == null)
		{
			instance = new ModelRenderer();
			ForestryAPI.modleRenderer = instance;
		}
		return instance;
	}

	private static final class CachedModel implements IBakedModel
	{
		List<BakedQuad>[] faces = new List[6];
		List<BakedQuad> general;

		public CachedModel()
		{
			general = new ArrayList<BakedQuad>();
			for ( EnumFacing f : EnumFacing.VALUES )
				faces[f.ordinal()] = new ArrayList<BakedQuad>();
		}
		
		@Override
		public boolean isGui3d()
		{
			return true;
		}

		@Override
		public boolean isBuiltInRenderer()
		{
			return false;
		}

		@Override
		public boolean isAmbientOcclusion()
		{
			return true;
		}

		@Override
		public TextureAtlasSprite getTexture()
		{
			return null;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms()
		{
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public List getGeneralQuads()
		{
			return general;
		}

		@Override
		public List getFaceQuads(
				EnumFacing p_177551_1_ )
		{
			return faces[p_177551_1_.ordinal()];
		}
	}

	public double renderMinX;
	public double renderMaxX;
	
	public double renderMinY;
	public double renderMaxY;
	
	public double renderMinZ;
	public double renderMaxZ;
	
	public ISprite overrideBlockTexture;
	
	CachedModel generatedModel = new CachedModel();

	// used to create faces...
	final FaceBakery faceBakery = new FaceBakery();

	float tx=0,ty=0,tz=0;
	final float[] defUVs = new float[] { 0, 0, 1, 1 };

	@Override
	public void setRenderBoundsFromBlock(Block block)
	{
		if ( block == null ) return;
		
		renderMinX = block.getBlockBoundsMinX();
		renderMinY = block.getBlockBoundsMinY();
		renderMinZ = block.getBlockBoundsMinZ();
		renderMaxX = block.getBlockBoundsMaxX();
		renderMaxY = block.getBlockBoundsMaxY();
		renderMaxZ = block.getBlockBoundsMaxZ();
	}

	@Override
	public void setRenderBounds(double d, double e, double f, double g, double h, double i )
	{
		renderMinX = d;
		renderMinY = e;
		renderMinZ = f;
		renderMaxX = g;
		renderMaxY = h;
		renderMaxZ = i;
	}

	int color = -1;
	
	@Override
	public void setBrightness(int i )
	{
		brightness=i;
	}

	@Override
	public void setColorRGBA_F(
			int r,
			int g,
			int b,
			float a )
	{
		int alpha = ( int ) ( a * 0xff );
		color = alpha << 24 |
				r << 16 |
				b << 8 |
				b;
	}

	@Override
	public void setColorOpaque_I(
			int whiteVariant )
	{
		int alpha = 0xff;
		color = //alpha << 24 |
				whiteVariant;
	}
	@Override
	public void setColorOpaque(
			int r,
			int g,
			int b )
	{
		int alpha = 0xff;
		color =// alpha << 24 |
				r << 16 |
				g << 8 |
				b;
	}
	
	@Override
	public void setColorOpaque_F(
			int r,
			int g,
			int b )
	{
		int alpha = 0xff;
		color = //alpha << 24 |
				Math.min( 0xff, Math.max( 0, r ) ) << 16 |
				Math.min( 0xff, Math.max( 0, g ) ) << 8 |
				Math.min( 0xff, Math.max( 0, b ) );
	}

	@Override
	public void setColorOpaque_F(float rf, float bf, float gf )
	{
		int r = (int)( rf * 0xff );
		int g = (int)( gf * 0xff );
		int b = (int)( bf * 0xff );
		int alpha = 0xff;
		color = //alpha << 24 |
				Math.min( 0xff, Math.max( 0, r ) ) << 16 |
				Math.min( 0xff, Math.max( 0, g ) ) << 8 |
				Math.min( 0xff, Math.max( 0, b ) );
	}

	int point =0;
	int brightness = -1;
	float[][] points = new float[4][];
	
	@Override
	public void addVertexWithUV(EnumFacing face, double x, double y, double z, double u, double v )
	{
		points[point++] = new float[]{ (float)x+tx, (float)y+ty, (float)z+tz, (float)u, (float)v };
		
		if ( point == 4 )
		{
			brightness = -1;
			int[] vertData = new int[]{
				Float.floatToRawIntBits( points[0][0] ),
				Float.floatToRawIntBits( points[0][1] ),
				Float.floatToRawIntBits( points[0][2] ),
				brightness,
				Float.floatToRawIntBits( points[0][3] ),
				Float.floatToRawIntBits( points[0][4] ),
				0,

				Float.floatToRawIntBits( points[1][0] ),
				Float.floatToRawIntBits( points[1][1] ),
				Float.floatToRawIntBits( points[1][2] ),
				brightness,
				Float.floatToRawIntBits( points[1][3] ),
				Float.floatToRawIntBits( points[1][4] ),
				0,
				
				Float.floatToRawIntBits( points[2][0] ),
				Float.floatToRawIntBits( points[2][1] ),
				Float.floatToRawIntBits( points[2][2] ),
				brightness,
				Float.floatToRawIntBits( points[2][3] ),
				Float.floatToRawIntBits( points[2][4] ),
				0,			
				
				Float.floatToRawIntBits( points[3][0] ),
				Float.floatToRawIntBits( points[3][1] ),
				Float.floatToRawIntBits( points[3][2] ),
				brightness,
				Float.floatToRawIntBits( points[3][3] ),
				Float.floatToRawIntBits( points[3][4] ),
				0,
			};
			
			generatedModel.general.add( new IColoredBakedQuad.ColoredBakedQuad( vertData, color, face ));
			
			point=0;
		}
	}

	@Override
	public boolean renderStandardBlock(Block block, BlockPos pos, ISprite[] textures)
	{
		setRenderBoundsFromBlock(block);

		setColorOpaque_I(0xffffff);

		renderFaceXNeg(pos, textures[EnumFacing.WEST.ordinal()]);
		renderFaceXPos(pos, textures[EnumFacing.EAST.ordinal()]);
		renderFaceYNeg(pos, textures[EnumFacing.DOWN.ordinal()]);
		renderFaceYPos(pos, textures[EnumFacing.UP.ordinal()]);
		renderFaceZNeg(pos, textures[EnumFacing.NORTH.ordinal()]);
		renderFaceZPos(pos, textures[EnumFacing.SOUTH.ordinal()]);
		
		return false;
	}
	
	@Override
	public boolean renderStandardBlock(Block block, BlockPos pos, ISprite texture) {
		setRenderBoundsFromBlock(block);

		setColorOpaque_I(0xffffff);

		renderFaceXNeg(pos, texture);
		renderFaceXPos(pos, texture);
		renderFaceYNeg(pos, texture);
		renderFaceYPos(pos, texture);
		renderFaceZNeg(pos, texture);
		renderFaceZPos(pos, texture);
		
		return false;
	}

	@Override
	public void setTranslation(int x, int y, int z )
	{
		tx=x;
		ty=y;
		tz=z;
	}

	@Override
	public boolean isAlphaPass()
	{
		return MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.TRANSLUCENT;
	}
	
	final float quadsUV[] = new  float[] { 0,0,1,1,0,0,1,1};
	public EnumSet<EnumFacing> renderFaces = EnumSet.allOf(EnumFacing.class);
	public boolean flipTexture=false;
	private List<RenderFace> faces = new ArrayList();
	
	private float[] getFaceUvs(final EnumFacing face, final Vector3f to_16, final Vector3f from_16)
	{
		float from_a = 0;
		float from_b = 0;
		float to_a = 0;
		float to_b = 0;

		switch ( face )
		{
			case UP:
				from_a = from_16.x / 16.0f;
				from_b = from_16.z / 16.0f;
				to_a = to_16.x / 16.0f;
				to_b = to_16.z / 16.0f;
				break;
			case DOWN:
				from_a = from_16.x / 16.0f;
				from_b = from_16.z / 16.0f;
				to_a = to_16.x / 16.0f;
				to_b = to_16.z / 16.0f;
				break;
			case SOUTH:
				from_a = from_16.x / 16.0f;
				from_b = from_16.y / 16.0f;
				to_a = to_16.x / 16.0f;
				to_b = to_16.y / 16.0f;
				break;
			case NORTH:
				from_a = from_16.x / 16.0f;
				from_b = from_16.y / 16.0f;
				to_a = to_16.x / 16.0f;
				to_b = to_16.y / 16.0f;
				break;
			case EAST:
				from_a = from_16.y / 16.0f;
				from_b = from_16.z / 16.0f;
				to_a = to_16.y / 16.0f;
				to_b = to_16.z / 16.0f;
				break;
			case WEST:
				from_a = from_16.y / 16.0f;
				from_b = from_16.z / 16.0f;
				to_a = to_16.y / 16.0f;
				to_b = to_16.z / 16.0f;
				break;
			default:
		}

		from_a = 1.0f - from_a;
		from_b = 1.0f - from_b;
		to_a = 1.0f - to_a;
		to_b = 1.0f - to_b;

		final float[] afloat = new float[] {// :P
		16.0f * ( quadsUV[0] + quadsUV[2] * from_a + quadsUV[4] * from_b ), // 0
		16.0f * ( quadsUV[1] + quadsUV[3] * from_a + quadsUV[5] * from_b ), // 1

		16.0f * ( quadsUV[0] + quadsUV[2] * to_a + quadsUV[4] * from_b ), // 2
		16.0f * ( quadsUV[1] + quadsUV[3] * to_a + quadsUV[5] * from_b ), // 3

		16.0f * ( quadsUV[0] + quadsUV[2] * to_a + quadsUV[4] * to_b ), // 2
		16.0f * ( quadsUV[1] + quadsUV[3] * to_a + quadsUV[5] * to_b ), // 3

		16.0f * ( quadsUV[0] + quadsUV[2] * from_a + quadsUV[4] * to_b ), // 0
		16.0f * ( quadsUV[1] + quadsUV[3] * from_a + quadsUV[5] * to_b ), // 1
		};

		return afloat;
	}
	
	@Override
	public void renderFaceXNeg(BlockPos pos, ISprite lights)
	{		
		boolean isEdge = renderMinX < 0.0001;
		Vector3f to = new Vector3f( (float)renderMinX* 16.0f, (float)renderMinY* 16.0f, (float)renderMinZ * 16.0f);
		Vector3f from = new Vector3f( (float)renderMinX* 16.0f, (float)renderMaxY* 16.0f, (float)renderMaxZ * 16.0f);

		final EnumFacing myFace = EnumFacing.WEST;
		addFace(myFace, isEdge,to,from,defUVs,lights );
	}
	
	@Override
	public void renderFaceYNeg(BlockPos pos, ISprite lights)
	{		
		boolean isEdge = renderMinY < 0.0001;
		Vector3f to = new Vector3f( (float)renderMinX* 16.0f, (float)renderMinY* 16.0f, (float)renderMinZ* 16.0f );
		Vector3f from = new Vector3f( (float)renderMaxX* 16.0f, (float)renderMinY* 16.0f, (float)renderMaxZ* 16.0f );

		final EnumFacing myFace = EnumFacing.DOWN;
		addFace(myFace, isEdge,to,from,defUVs, lights );
	}

	@Override
	public void renderFaceZNeg(BlockPos pos, ISprite lights)
	{		
		boolean isEdge = renderMinZ < 0.0001;
		Vector3f to = new Vector3f( (float)renderMinX* 16.0f, (float)renderMinY* 16.0f, (float)renderMinZ* 16.0f );
		Vector3f from = new Vector3f( (float)renderMaxX* 16.0f, (float)renderMaxY* 16.0f, (float)renderMinZ* 16.0f );

		final EnumFacing myFace = EnumFacing.NORTH;
		addFace(myFace, isEdge,to,from,defUVs, lights );
	}

	@Override
	public void renderFaceYPos(BlockPos pos, ISprite lights)
	{		
		boolean isEdge = renderMaxY > 0.9999;
		Vector3f to = new Vector3f( (float)renderMinX* 16.0f, (float)renderMaxY* 16.0f, (float)renderMinZ* 16.0f );
		Vector3f from = new Vector3f( (float)renderMaxX* 16.0f, (float)renderMaxY* 16.0f, (float)renderMaxZ * 16.0f);

		final EnumFacing myFace = EnumFacing.UP;
		addFace(myFace, isEdge,to,from,defUVs,lights );
	}

	@Override
	public void renderFaceZPos(BlockPos pos, ISprite lights)
	{
		boolean isEdge = renderMaxZ > 0.9999;
		Vector3f to = new Vector3f( (float)renderMinX* 16.0f, (float)renderMinY* 16.0f, (float)renderMaxZ* 16.0f );
		Vector3f from = new Vector3f( (float)renderMaxX* 16.0f, (float)renderMaxY* 16.0f, (float)renderMaxZ* 16.0f );

		final EnumFacing myFace = EnumFacing.SOUTH;
		addFace(myFace, isEdge,to,from,defUVs,lights );
	}

	@Override
	public void renderFaceXPos(BlockPos pos, ISprite lights )
	{
		boolean isEdge = renderMaxX > 0.9999;
		Vector3f to = new Vector3f( (float)renderMaxX * 16.0f, (float)renderMinY* 16.0f, (float)renderMinZ* 16.0f );
		Vector3f from = new Vector3f( (float)renderMaxX* 16.0f, (float)renderMaxY* 16.0f, (float)renderMaxZ* 16.0f );

		final EnumFacing myFace = EnumFacing.EAST;
		addFace(myFace, isEdge,to,from,defUVs, lights );
	}

	private void addFace(EnumFacing face , boolean isEdge, Vector3f to, Vector3f from, float[] defUVs2, ISprite texture )
	{
		if ( overrideBlockTexture != null )
			texture = overrideBlockTexture;
		
		faces.add(new RenderFace(face,isEdge,color,to,from,defUVs2, texture.getSprite()));
	}

	EnumFacing currentFace = EnumFacing.UP;
	
	@Override
	public void setNormal(float x, float y, float z)
	{
		if ( x > 0.5 ) currentFace = EnumFacing.EAST;
		if ( x < -0.5 ) currentFace = EnumFacing.WEST;
		if ( y > 0.5 ) currentFace = EnumFacing.UP;
		if ( y < -0.5 ) currentFace = EnumFacing.DOWN;
		if ( z > 0.5 ) currentFace = EnumFacing.SOUTH;
		if ( z < -0.5 ) currentFace = EnumFacing.NORTH;
	}

	@Override
	public void setOverrideBlockTexture(ISprite object)
	{
		overrideBlockTexture = object;		
	}

	@Override
	public IBakedModel finalizeModel(boolean Flip)
	{
		ModelRotation mr = ModelRotation.X0_Y0;
		
		if ( Flip )
			  mr = ModelRotation.X0_Y180;
		
		for ( RenderFace face : faces )
		{
			final EnumFacing myFace = face.face;
			final float[] uvs = getFaceUvs( myFace, face.from, face.to );
			
			final BlockFaceUV uv = new BlockFaceUV( uvs, 0 );
			final BlockPartFace bpf = new BlockPartFace( myFace, face.color, "", uv );
	
			BakedQuad bf = faceBakery.makeBakedQuad( face.to, face.from, bpf, face.spite, myFace, mr, null, true, true );
			bf = new IColoredBakedQuad.ColoredBakedQuad( bf.getVertexData(), face.color, bf.getFace() );
			
			if ( face.isEdge )
				this.generatedModel.getFaceQuads( myFace ).add( bf );
			else
				this.generatedModel.getGeneralQuads().add( bf );
		}
		return generatedModel;
	}
	
	@Override
	public double getRenderMinX() {
		return renderMinX;
	}
	
	@Override
	public double getRenderMinY() {
		return renderMinY;
	}
	
	@Override
	public double getRenderMinZ() {
		return renderMinZ;
	}
	
	@Override
	public double getRenderMaxX() {
		return renderMaxX;
	}
	
	@Override
	public double getRenderMaxY() {
		return renderMaxY;
	}
	
	@Override
	public double getRenderMaxZ() {
		return renderMaxZ;
	}

}
