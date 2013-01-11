package ljdp.voxelcoin.mock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import static org.mockito.Mockito.*;

public class Mock {
	
	private static int mockPlayerId = 0;
	
	public static EntityPlayer createMockPlayer() {
		EntityPlayer mockPlayer = mock(EntityPlayerMP.class);
		when(mockPlayer.getEntityName()).thenReturn("MockPlayer" + mockPlayerId);
		mockPlayerId++;
		return mockPlayer;
	}
}
