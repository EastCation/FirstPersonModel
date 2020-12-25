package dev.tr7zw.firstperson.fabric.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import dev.tr7zw.firstperson.fabric.FirstPersonModelMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Matrix4f;

/**
 * Detects when the player is rendered and triggers the correct changes
 *
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	private MinecraftClient client = MinecraftClient.getInstance();
	
	@Inject(at = @At("HEAD"), method = "renderEntity")
	private void renderEntity(Entity entity_1, double double_1, double double_2, double double_3, float float_1,
			MatrixStack matrixStack_1, VertexConsumerProvider vertexConsumerProvider_1, CallbackInfo info) {
		if (FirstPersonModelMod.hideNextHeadArmor)
			FirstPersonModelMod.hideNextHeadArmor = false;
		if (FirstPersonModelCore.instance.isFixActive(entity_1, matrixStack_1))
			FirstPersonModelMod.hideNextHeadArmor = true; // if I don't wear any armor head then another helmet will be
															// hidden without this

		if (client.options.getPerspective() != Perspective.FIRST_PERSON)
			return;
		if (entity_1 instanceof AbstractClientPlayerEntity) {
			if (!((PlayerEntity) entity_1).isMainPlayer())
				return;
			FirstPersonModelMod.isRenderingPlayer = true;
		}
	}

	@Inject(method = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;isThirdPerson()Z"))
	private void redirect(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline,
			Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f,
			CallbackInfo ci) {
		FirstPersonModelCore.instance.getWrapper().isThirdPersonTrigger(matrices);
	}
}
