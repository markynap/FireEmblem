package gameMain;

import java.awt.Graphics;

import gameMain.Game.STATE;

public class GameRenderer {

	private Game game;
	
	public GameRenderer(Game game) {
		this.game = game;
	}
	
	
	public void render(Graphics g, STATE gameState) {
		
		switch (gameState) {
		
		case Menu: 
			game.menu.render(g);
			break;
			
		case Info: 
			game.chapterOrganizer.render(g);
			game.playerGFX.render(g);
			break;
			
		case AdvInfo: 
			game.chapterOrganizer.render(g);
			game.playerGFX.renderAdv(g);
			break;
			
		case PopUpMenu: 
			game.chapterOrganizer.render(g);
			if (game.PUM != null) game.PUM.render(g);
			break;
			
		case EnemyPhase:
			game.chapterOrganizer.render(g);
			break;
			
		case LoseGame: 
			game.renderLoseGame(g);
			break;
			
		case StartScreen: 
			game.renderStartScreen(g);
			break;
			
		case ChapterDesignMenu: 
			if (game.designer != null) game.designer.menu.render(g);
			break;
			
		case ChapterDesign: 
			if (game.designer != null) game.designer.render(g);
			break;
			
		case AttackStage: 
			game.AttackManager.renderAttackAnimation(g);
			break;
			
		case ChapterChoose: 
			if (game.chapterChooser != null) game.chapterChooser.render(g);
			break;
			
		case LoadScreen: 
			game.loadScreen.render(g);
			break;
			
		case ReturnToMenu: 
			game.chapterOrganizer.render(g);
			game.renderMenuPopUp(g);
			break;
			
		case LoadGame: 
			game.gameLoader.render(g);
			break;
			
		case GainEXP: 
			game.chapterOrganizer.render(g);
			game.levelUpLoader.render(g);
			break;
			
		case LevelUp:
			game.chapterOrganizer.render(g);
			game.levelUpLoader.render(g);
			break;
			
		case TradeState: 
			game.chapterOrganizer.render(g);
			if (game.tradeMenu != null) game.tradeMenu.render(g);
			break;
			
		case weaponSelection: 
			game.chapterOrganizer.render(g);
			game.playerGFX.renderWeaponSelection(g);
			break;
			
		case outGameCutScene: 
			game.cutScenes.render(g);
			break;
			
		case optionsMenu: 
			game.chapterOrganizer.render(g);
			if (game.optionsMenu != null) game.optionsMenu.render(g);
			break;
			
		case inGameCutScene: 
			game.chapterOrganizer.render(g);
			game.cutScenes.render(g);
			break;
			
		case itemReceived: 
			game.chapterOrganizer.render(g);
			game.chapterOrganizer.currentMap.renderItemReceived(g);
			break;
			
		case viewInfo: 
			game.chapterOrganizer.render(g);
			game.playerGFX.render(g);
			break;	
			
		case viewAdvInfo: 
			game.chapterOrganizer.render(g);
			game.playerGFX.renderAdv(g);
			break;	
			
		case Armory: 
			game.armoryHandler.render(g);
			break;	
			
		case BattlePreparations: 
			game.chapterOrganizer.battlePrep.render(g);
			break;	
			
		case Promotion: 
			game.promotionManager.render(g);
			break;	
			
		case Tutorial: 
			game.tutorialDisplay.render(g);
			break;	
			
		case playerFollowChoice: 
			game.playerFollow.render(g);
			break;	
			
		default:
			game.chapterOrganizer.render(g);
			break;
		}
		
	/*	if (gameState == STATE.Game) {
			game.chapterOrganizer.render(g);
		}
		if (gameState == STATE.MoveState) {
			game.chapterOrganizer.render(g);
		} else if (gameState == STATE.AttackState) {
			game.chapterOrganizer.render(g);
		} else if (gameState == STATE.EnemyPhase) {
			game.chapterOrganizer.render(g);
		} if (gameState == STATE.EnemyChoice) {
			game.chapterOrganizer.render(g);
		} if (gameState == STATE.keyOpeningState) {
			game.chapterOrganizer.render(g);
		}if (gameState == STATE.skillUse) {
			game.chapterOrganizer.render(g);
		}

		if (gameState == STATE.viewInfo) {
			game.chapterOrganizer.render(g);
			game.playerGFX.render(g);
		} else if (gameState == STATE.viewAdvInfo) {
			game.chapterOrganizer.render(g);
			game.playerGFX.renderAdv(g);
		} else if (gameState == STATE.Armory) {
			game.armoryHandler.render(g);
		} else if (gameState == STATE.BattlePreparations) {
			game.chapterOrganizer.battlePrep.render(g);
		} else if (gameState == STATE.Promotion) {
			game.promotionManager.render(g);
		} else if (gameState == STATE.Tutorial) {
			game.tutorialDisplay.render(g);
		} else if (gameState == STATE.playerFollowChoice) {
			game.playerFollow.render(g);
		}
		
		
		else {
			game.chapterOrganizer.render(g);
		}*/
	}
}
