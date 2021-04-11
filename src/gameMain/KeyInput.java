package gameMain;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import enemy_ai.PathGenerator;
import characters.ConfessedAlly;
import characters.Player;
import characters.SummonedUnit;
import gameMain.Game.STATE;
import gameMain.Menu.MODE;
import graphics.AttackMenu;
import graphics.BattlePreparationsMenu.PreparationState;
import graphics.PopUpMenu;
import items.Item;
import items.UtilityItem;
import tiles.ArmoryTile;
import tiles.ChestTile;
import tiles.DamagedWallTile;
import tiles.FloorTile;
import tiles.GrassTile;
import tiles.MountainTile;
import tiles.Tile;
import tiles.VendorTile;
import tiles.Village;

public class KeyInput extends KeyAdapter {

	public Game game;
	public char key;
	public int keyCode;
	public ChapterMap currentMap;
	public Tile currentTile;
	public Player currentPlayer;

	public KeyInput(Game game) {
		this.game = game;
	}

	public void keyPressed(KeyEvent e) {
		
		if (game.gameState == STATE.StartScreen) {
			game.timekeep.startSession();
			game.setGameState(STATE.Menu);
			return;
		}
		if (game.gameState == STATE.LoseGame) {
			game.setGameState(STATE.StartScreen);
			return;
		}
		
		
		
		key = e.getKeyChar();
		keyCode = e.getExtendedKeyCode();
		currentMap = game.chapterOrganizer.currentMap;
		
		if (keyCode == KeyEvent.VK_ENTER) {
			key = 'a';
		}
		
		if (key == 'A') {
			key = 'a';
		}
		
		if (currentMap != null) currentTile = currentMap.currentTile;
		
		if (key == 's') {
			if (game.gameState == STATE.EnemyPhase || game.gameState == STATE.EnemyChoice
					|| game.gameState == STATE.outGameCutScene || game.gameState == STATE.inGameCutScene) return;
			if (game.gameState == STATE.ChapterChoose) {
				game.menu.setMenuMode(MODE.Main);
				game.setGameState(STATE.Menu);
				return;
			}
			if (game.gameState == STATE.playerFollowChoice) return;
			if (game.gameState == STATE.TradeState) {
				if (game.tradeMenu != null) {
					if (game.tradeMenu.inOtherTradersSpace()) {
						game.tradeMenu.setInOtherTradingSpace(false);
						return;
					}
				}
			}
		
		if (game.pathGenerator != null)	{
			game.pathGenerator.resetTiles();
			game.pathGenerator.eraseScopes();
			currentMap.nullAttackMenu();
		}
		if (game.gameState == STATE.Promotion) return;
		if (game.gameState == STATE.GainEXP) return;
		if (game.gameState == STATE.Info) {
			if (game.playerGFX.inItemOptions) {
				game.playerGFX.setItemOptions(false);
				return;
			}
		} else if (game.gameState == STATE.Tutorial) {
			game.tutorialDisplay.handleBackCommand();
			return;
		}
		
		
		if (game.gameState == STATE.BattlePreparations) {
			if (game.chapterOrganizer.battlePrep.prepState == PreparationState.AllyInfo) {
				if (game.tradeMenu != null) {
					game.tradeMenu = null;
					game.chapterOrganizer.battlePrep.setSelectedPlayer(null);
					return;
				}
				if (game.chapterOrganizer.battlePrep.hasSelectedPlayer()) {
					game.chapterOrganizer.battlePrep.setSelectedPlayer(null);
					return;
				}
			} else if (game.chapterOrganizer.battlePrep.prepState == PreparationState.AllyPositions) {
				if (game.chapterOrganizer.battlePrep.hasSelectedPlayer()) {
					game.chapterOrganizer.battlePrep.setSelectedPlayer(null);
					return;
				}
			}
			game.chapterOrganizer.battlePrep.setPrepState(PreparationState.MainMenu);
			game.chapterOrganizer.battlePrep.setSelectedPlayer(null);
			return;
		}
		
		currentMap.nullAttackMenu();
		game.tradeMenu = null;
		if (game.gameState == STATE.LoadGame) {
			if (game.gameLoader.isSaving) return;
			game.setGameState(STATE.Menu); 
		}
		if (game.playerGFX!= null) game.playerGFX.setItemOptions(false);
		if (game.gameState == STATE.Menu) {
			if (game.menu.menuMode == MODE.DifficultySelection || game.menu.menuMode == MODE.LoadLevelSelection) {
				game.menu.setMenuMode(MODE.Main);
			}
			return;
		}
		if (game.gameState == STATE.AttackStage) {
			game.chapterOrganizer.checkForLoss();
			game.handleEXPDecision();
			return;
			
		} else if (game.gameState == STATE.LevelUp) {
			game.levelUpLoader.handleBackCommand();
			return;
		} 
				
		if (game.gameState == STATE.TradeState || game.gameState == STATE.weaponSelection||game.gameState == STATE.Info 
				|| game.gameState == STATE.AdvInfo || game.gameState == STATE.AttackState || game.gameState == STATE.skillUse || game.gameState == STATE.playerCarry
				|| game.gameState == STATE.keyOpeningState || game.gameState == STATE.TalkState) {
			// go back to pop up
			if (currentPlayer.teamID.equalsIgnoreCase("Ally")) {
			currentMap.findRegion();
			if (currentPlayer.canMAUT()) {
				setPopUpMenu();
			} else game.setGameState(STATE.Game);
			} else game.setGameState(STATE.Game);
			return;
		} 
		
		
		if (game.gameState == STATE.Armory) {
			
			if (game.armoryHandler.inItemPurchaseMode()) {
				game.armoryHandler.setItemPuchaseMode(false);
			} else {
				if (game.armoryHandler.playerPurchasedItem()) {
					game.armoryHandler.getShopper().setMAUT(false);
					game.setGameState(STATE.Game);
				} else {
					setPopUpMenu();
				}
			}
			return;
			
		}
		
		if (game.gameState != STATE.PopUpMenu) {
			game.setGameState(STATE.Game);
		}
		
		}
		
		if (game.gameState == STATE.Game) {
			
			moveCurrentTile();
			
			if (keyCode == KeyEvent.VK_ESCAPE) {
				game.setGameState(STATE.ReturnToMenu);
			}
						
			if (key == 'a') {
				currentPlayer = currentTile.carrier;
				if (currentPlayer == null) {
					setPopUpMenu();
				} else {
					if (!currentPlayer.teamID.equalsIgnoreCase("Ally")) {
						// enemy movement and attack range
						game.setPathGenerator(new PathGenerator(game, currentTile, currentPlayer.getMOV()));
						game.pathGenerator.resetTiles();
						game.pathGenerator.setAllPathableTiles(currentPlayer.isFlier);
						return;
						
					}
					if (!currentPlayer.canMove) {
						setPopUpMenu();
					} else {
					game.setPathGenerator(new PathGenerator(game, currentTile, currentPlayer.getMOV()));
					game.pathGenerator.resetTiles();
					game.pathGenerator.setAllPathableTiles(currentPlayer.isFlier);
					game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
					game.setGameState(STATE.MoveState);
					}
				}
			} else if (key == 'q') {
				currentMap.findAllyWithMoves();
			} else if (key == 'w') {
				if (currentTile.carrier == null) return;
				game.playerGFX.setPlayer(currentTile.carrier);
				game.setGameState(STATE.viewInfo);
			} else if (key == '`') {
				game.setGameState(STATE.MiniMapView);
			}
			
			
			
		} else if (game.gameState == STATE.MiniMapView) {
			
			game.setGameState(STATE.Game);
			
		} else if (game.gameState == STATE.Info) {
			
			if (key == 'q') {
				game.setGameState(STATE.AdvInfo);
			} else if (key == 'a') {
				if (!currentPlayer.teamID.equalsIgnoreCase("Ally")) return;
				game.SFX.playSelect();
				if (!game.playerGFX.inItemOptions) {
					game.playerGFX.setItemOptions(true);
				} else {
					if (game.playerGFX.itemOptionIndex == 0) {
						if (game.playerGFX.currentItem.category.equalsIgnoreCase("Utility")) {
							UtilityItem it = (UtilityItem) game.playerGFX.currentItem;
							it.use();
							game.playerGFX.setItemOptions(false);
						} else {
							game.playerGFX.wallet.equipt(game.playerGFX.currentItem);
							game.playerGFX.setItemOptions(false);
						}
						
					} else if (game.playerGFX.itemOptionIndex == 1) {
						currentTile.carrier.removeItem(game.playerGFX.currentItem);
						game.playerGFX.decWeaponIndex();
						game.playerGFX.setItemOptions(false);
					}
				}
			
			} else if (keyCode == KeyEvent.VK_DOWN) {
				game.SFX.playCursor();
				if (!game.playerGFX.inItemOptions) game.playerGFX.incWeaponIndex();
				else game.playerGFX.incItemOptionIndex(1);
			} else if (keyCode == KeyEvent.VK_UP) {
				game.SFX.playCursor();
				if (!game.playerGFX.inItemOptions) game.playerGFX.decWeaponIndex();
				else game.playerGFX.incItemOptionIndex(-1);
			} else {
				game.playerGFX.setItemOptions(false);
				if (game.pathGenerator != null) game.pathGenerator.resetTiles();
				
				if (currentPlayer.teamID.equalsIgnoreCase("Ally")) {
					game.setGameState(STATE.Game);
					setPopUpMenu();
				} else {
					currentMap.nullAttackMenu();
					game.setGameState(STATE.Game);
				}
			}
			
			
		} else if (game.gameState == STATE.AdvInfo) {
			
			if (key == 'q') game.setGameState(STATE.Info);
			else {
				if (currentPlayer.teamID.equalsIgnoreCase("Ally")) {
					game.setGameState(STATE.Game);
					setPopUpMenu();
				} else {
					currentMap.nullAttackMenu();
					game.setGameState(STATE.Game);
				}
			}
			
		} else if (game.gameState == STATE.PopUpMenu) {
			
			
			if (key == 's') {
				if (currentPlayer != null) {
					if (currentPlayer.canMove && currentPlayer.canAttack && currentPlayer.canTrade && currentPlayer.canUse) { 
						// we will just deselect them and return to Game State
						currentMap.nullAttackMenu();
						game.setGameState(STATE.Game);	
						return;
					}
					if (!currentPlayer.canMove) {
						if (currentPlayer.canAttack && currentPlayer.canUse && currentPlayer.canTrade) {
							// we have moved, but not attacked, traded, or used anything, so we can return to previous tile
							if (currentPlayer.previousTile != null && !currentPlayer.previousTile.isOccupied()) {
								currentMap.move(currentPlayer, currentPlayer.previousTile);
								currentPlayer.setMAUT(true);
								currentMap.setCurrentTile(currentPlayer.currentTile);
								currentMap.nullAttackMenu();
								game.setPathGenerator(new PathGenerator(game, currentPlayer.currentTile, currentPlayer.getMOV()));
								game.pathGenerator.resetTiles();
								game.pathGenerator.setAllPathableTiles(currentPlayer.isFlier);
								game.chapterOrganizer.currentMap.selectedBoxTile = currentPlayer.currentTile;
								game.setGameState(STATE.MoveState);
								}	
					} else {
						// we make them wait, since we have either attacked, traded, or used, we cannot go back
						currentPlayer.setMAU(false);
						currentMap.nullAttackMenu();
						game.setGameState(STATE.Game);
						if (game.pathGenerator != null) game.pathGenerator.resetTiles();
						}
					}
				} else {
					// current player is null, we must have selected a random tile for options/end
					currentMap.nullAttackMenu();
					game.setGameState(STATE.Game);
					if (game.pathGenerator != null) game.pathGenerator.resetTiles();
				}
			}
			if (keyCode == KeyEvent.VK_UP) {
				game.SFX.playCursor();
				game.PUM.incSelectedOptions(-1);
			} else if (keyCode == KeyEvent.VK_DOWN) {
				game.SFX.playCursor();
				game.PUM.incSelectedOptions(1);
			}
			
			if (key == 'a') {
				game.SFX.playSelect();
				String optionChosen = game.PUM.getSelectedOption();
				if (optionChosen.equalsIgnoreCase("Sieze")) {
					if (!currentPlayer.teamID.equalsIgnoreCase("Ally")) return;
					game.endChapter();
				} else if (optionChosen.equalsIgnoreCase("Attack")) {
					
					game.pathGenerator.resetTiles();
					game.playerGFX.setPlayer(currentPlayer, game.chapterOrganizer.closestEnemyDistance(currentPlayer)-currentPlayer.bowExtention);
					game.playerGFX.setIsHealing(false);
					game.setGameState(STATE.weaponSelection);
					
				} else if (optionChosen.equalsIgnoreCase("Heal")) {
					
					
					game.pathGenerator.resetTiles();
					game.playerGFX.setPlayer(currentPlayer);
					game.playerGFX.setIsHealing(true);
					game.setGameState(STATE.weaponSelection);

				} else if (optionChosen.equalsIgnoreCase("Carry")) {
					
					game.setPathGenerator(new PathGenerator(game, currentTile, 1));
					game.pathGenerator.resetTiles();
					game.pathGenerator.setAllPathableTiles(false);
					game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
					game.setGameState(STATE.playerCarry);
					
				} else if (optionChosen.equalsIgnoreCase("Drop")) {
					
					game.setPathGenerator(new PathGenerator(game, currentTile, 1));
					game.pathGenerator.resetTiles();
					game.pathGenerator.setAllPathableTiles(false);
					game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
					game.setGameState(STATE.playerCarry);
					
				} else if (optionChosen.equalsIgnoreCase("Dance")) {
					
					game.setPathGenerator(new PathGenerator(game, currentTile, 1));
					game.pathGenerator.resetTiles();
					game.pathGenerator.setAllPathableTiles(false);
					game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
					game.setGameState(STATE.AttackState);
				
				} else if (optionChosen.equalsIgnoreCase("Items")) {
					
					game.playerGFX.setPlayer(currentTile.carrier);
					game.setGameState(STATE.Info);
					
				} else if (optionChosen.equalsIgnoreCase("Trade")) {
					
					if (currentPlayer == null) return;
					if (currentTile.carrier == null) return;
					if (!currentPlayer.canMove) if (!currentPlayer.canUse) return;
					if (game.pathGenerator != null) game.pathGenerator.resetTiles();
					game.setPathGenerator(new PathGenerator(game, currentTile, 1));
					game.pathGenerator.resetTiles();
					game.pathGenerator.setAllPathableTiles(true);
					game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
					game.setGameState(STATE.TradeState);
					
				} else if (optionChosen.equalsIgnoreCase("Wait")) {
					
					if (currentPlayer == null) return;
					currentPlayer.setMAU(false);
					currentMap.nullAttackMenu();
					game.setGameState(STATE.Game);
					if (game.pathGenerator != null) game.pathGenerator.resetTiles();
					
				} else if (optionChosen.equalsIgnoreCase("End")) {
					
					currentMap.nullAttackMenu();
					if (game.pathGenerator != null) game.pathGenerator.resetTiles();
					game.setGameState(STATE.Game);	
					currentMap.nextPhase();
					
				} else if (optionChosen.equalsIgnoreCase("Options")) {
					
					game.setOptionsMenuState();
					
				} else if (optionChosen.equalsIgnoreCase("Tutorial")) {
					
					game.setGameState(STATE.Tutorial);
					
				} else if (optionChosen.equalsIgnoreCase("Pick")) {
					game.setKeyUseState(null);
				} else if (optionChosen.equalsIgnoreCase("Visit")) {
					
					if (currentPlayer.currentTile.category.equalsIgnoreCase("Village")) {
					
						Village vill = (Village) currentPlayer.currentTile;
						vill.visit(currentPlayer);
					} else if (currentPlayer.currentTile.category.equalsIgnoreCase("Armory")) {
						
						ArmoryTile ar = (ArmoryTile)currentPlayer.currentTile;
						ar.visit(currentPlayer);
					} else if (currentPlayer.currentTile.category.equalsIgnoreCase("Vendor")) {
						
						VendorTile ar = (VendorTile)currentPlayer.currentTile;
						ar.visit(currentPlayer);
						
					}
				
				} else if (optionChosen.equalsIgnoreCase("Talk")) {
					
					game.setPathGenerator(new PathGenerator(game, currentTile, 1));
					game.pathGenerator.resetTiles();
					game.pathGenerator.setAllPathableTiles(true);
					game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
					game.setGameState(STATE.TalkState);
					return;
				}
				
				
				else if (optionChosen.equalsIgnoreCase("Skill")) {
					
					if (currentPlayer.skill.nameEquals("Divine Blessing")) {
						
						game.setPathGenerator(new PathGenerator(game, currentTile, 1));
						game.pathGenerator.resetTiles();
						game.pathGenerator.setAllPathableTiles(false);
						game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
						
					} else if (currentPlayer.skill.nameEquals("Soul Juice")) {
						
						game.setPathGenerator(new PathGenerator(game, currentTile, 1));
						game.pathGenerator.resetTiles();
						game.pathGenerator.setAllPathableTiles(false);
						game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
						
					} else if (currentPlayer.skill.nameEquals("Teleportation")) {
						
						game.setPathGenerator(new PathGenerator(game, currentTile, 150));
						game.pathGenerator.setAllPathableTiles(true);
						game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
						
					} else if (currentPlayer.skill.nameEquals("Summoning")) {
						
						game.setPathGenerator(new PathGenerator(game, currentTile, 1));
						game.pathGenerator.resetTiles();
						game.pathGenerator.setAllPathableTiles(false);
						game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
						
					} else if (currentPlayer.skill.nameEquals("Confession")) {
						
						game.setPathGenerator(new PathGenerator(game, currentTile, 1 + currentPlayer.confessionRange));
						game.pathGenerator.resetTiles();
						game.pathGenerator.setAllPathableTiles(true);
						game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
						
					} else if (currentPlayer.skill.nameEquals("Condar")) {
						
						game.setPathGenerator(new PathGenerator(game, currentTile, 2));
						game.pathGenerator.resetTiles();
						game.pathGenerator.setAllPathableTiles(true);
						
					} else if (currentPlayer.skill.nameEquals("Teraform")) {
						
						game.pathGenerator.resetTiles();
						game.pathGenerator.setTeraformMode();
						
					}
					
					game.setGameState(STATE.skillUse);
					
				}
				
			}
			
		} else if (game.gameState == STATE.viewInfo) {
			if (key =='q') game.setGameState(STATE.viewAdvInfo);
		} else if (game.gameState == STATE.viewAdvInfo) {
			if (key == 'q') game.setGameState(STATE.viewInfo);
		} else if (game.gameState == STATE.playerFollowChoice) {
			
			if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_LEFT) {
				game.playerFollow.swapOption();
			}
			if (key == 'a') {
				game.playerFollow.chooseFollowPath();
			}
			
		} else if (game.gameState == STATE.Promotion) {
			
			if (keyCode == KeyEvent.VK_UP) {
				game.promotionManager.incSelectedIndex(-1);
			} else if (keyCode == KeyEvent.VK_DOWN) {
				game.promotionManager.incSelectedIndex(1);
			}
			
			if (key == 'a') {
				game.promotionManager.selectPromotion();
			}
			
		} else if (game.gameState == STATE.Tutorial) {
			
			if (keyCode == KeyEvent.VK_DOWN) {
				game.tutorialDisplay.incSelectedIndex(1);
			} else if (keyCode == KeyEvent.VK_UP) {
				game.tutorialDisplay.incSelectedIndex(-1);
			}
			
			if (key == 'a') {
				
				if (game.tutorialDisplay.inCategoryMode()) {
					game.tutorialDisplay.setCategoryMode(false);
				}
				
			}
			
		}
		
		
		else if (game.gameState == STATE.playerCarry) {
			
			moveArrowTile(true);
			
			if (key == 'a') {
				if (game.pathGenerator.getTopTile().isOccupied()) {
					Player p = game.pathGenerator.getTopTile().carrier;
					if (p.name.equalsIgnoreCase(currentPlayer.name)) return;
					
					if (currentPlayer.playerCarried == null) {
						if (currentPlayer.CON + currentPlayer.getMountedAidBonus() > p.CON) {
							if (!p.isCarryingUnit) {
								currentPlayer.carryPlayer(p);
								game.pathGenerator.resetTiles();
								game.setGameState(STATE.Game);
							}
						}
					}
				} else {
					
					if (currentPlayer.playerCarried != null) {
						currentPlayer.dropCarriedPlayer(game.pathGenerator.getTopTile());
						game.pathGenerator.resetTiles();
						game.setGameState(STATE.Game);
					}
					
				}
			}
			
			
		}
		
		else if (game.gameState == STATE.skillUse) {
			
			if (currentPlayer == null) {
				System.out.println("Current player of the skill we are using is null in SkillUse Key Input"); 
				return;
			}
			
			if (currentPlayer.skill.nameEquals("Teleportation") || currentPlayer.skill.nameEquals("Summoning")
					|| currentPlayer.skill.nameEquals("Confession")) {
				moveArrowTile(true);
			} else if (currentPlayer.skill.nameEquals("Teraform")) {
				
				if (keyCode == KeyEvent.VK_RIGHT) {
					game.pathGenerator.moveTeraformation('E');
				} else if (keyCode == KeyEvent.VK_LEFT) {
					game.pathGenerator.moveTeraformation('W');
				} else if (keyCode == KeyEvent.VK_UP) {
					game.pathGenerator.moveTeraformation('N');					
				} else if (keyCode == KeyEvent.VK_DOWN) {
					game.pathGenerator.moveTeraformation('S');
				}
				
			}
			
			if (key == 'a') {
				// use skill
				ArrayList<Player> list;
				switch (currentPlayer.skill.getName()) {
				
				
				case "Divine Blessing":
								currentPlayer.castDivineBlessing();
								
								break;	
				
				case "Soul Juice":
								list = game.chapterOrganizer.getAdjacentAllies(currentPlayer);
								if (list.isEmpty()) return;
								for (int i = 0; i < list.size(); i++) {
									list.get(i).setMAUT(true);
								}
								break;
					
				case "Teleportation": 
								if (game.pathGenerator.getTopTile().isOccupied()) return;
								game.chapterOrganizer.currentMap.move(currentPlayer, game.pathGenerator.getTopTile());
								game.chapterOrganizer.currentMap.setCurrentTile(game.pathGenerator.getTopTile());
								game.pathGenerator.resetTiles();
								game.SFX.playSelect();
								break;
				
				case "Summoning":
								if (game.pathGenerator.getTopTile().isOccupied()) return;
								if (game.chapterOrganizer.hasSummonedUnit()) {
									game.chapterOrganizer.summonedUnit.die();
								}
								Tile summonTile = game.pathGenerator.getTopTile();
								SummonedUnit unit = new SummonedUnit(summonTile.x, summonTile.y,game, game.chapterOrganizer.currentChapter);
								summonTile.setCarrier(unit);
								game.pathGenerator.resetTiles();
								break;
				
				case "Confession":
								if (game.pathGenerator.getTopTile().isOccupied()) {
									Player confessed = game.pathGenerator.getTopTile().carrier;
									if (confessed.teamID.equalsIgnoreCase(currentPlayer.teamID)) return;
									if (confessed.isBoss) return;
									if (confessed.level - currentPlayer.level >= 25) return;
									// get our confession tile
									Tile confessTile = game.pathGenerator.getTopTile();
									ConfessedAlly newAlly = new ConfessedAlly(confessed.stats, confessed.xPos, confessed.yPos, game, confessed.wallet, confessed.image);
									confessed.die();
									newAlly.skill = confessed.skill;
									newAlly.isFlier = confessed.isFlier;
									game.chapterOrganizer.addAlly(newAlly);
									confessTile.setCarrier(newAlly);
									newAlly.setCurrentTile(confessTile);
									game.pathGenerator.resetTiles();
									game.setPlayerForEXP(currentPlayer, currentPlayer.EXP);
									currentPlayer.addEXP(50);
									game.startEXPScene(false);
								} else return;
								break;
				
				case "Condar":
								list = game.chapterOrganizer.listOfOpposingUnitsInRange(currentPlayer, 2);
								if (list.isEmpty()) return;
								for (int i = 0; i < list.size(); i++) {
									Player p = list.get(i);
									if (p.isBoss) continue;
									if (p.level - currentPlayer.level >= 25) continue;
									Tile confessTile = p.currentTile;
									ConfessedAlly newAlly = new ConfessedAlly(p.stats, p.xPos, p.yPos, game, p.wallet, p.image);
									p.die();
									newAlly.skill = p.skill;
									game.chapterOrganizer.addAlly(newAlly);
									confessTile.setCarrier(newAlly);
									newAlly.setCurrentTile(confessTile);
									game.pathGenerator.resetTiles();
									game.setPlayerForEXP(currentPlayer, currentPlayer.EXP);
									currentPlayer.addEXP(75);
									game.startEXPScene(false);
								}
								break;
				
				case "Teraform":
								for (int i = 0; i < game.pathGenerator.terraformedTiles.size(); i++) {
									Tile t = game.pathGenerator.terraformedTiles.get(i);
									if (t.isCrossable) {
										// make t not crossable
										currentMap.setTile(t, new MountainTile(t.x, t.y, currentMap), 4);

									} else {
										currentMap.setTile(t, new GrassTile(t.x, t.y, currentMap), 0);

									}
								}
					
								currentMap.setCurrentTile(game.pathGenerator.terraformedTiles.get(4));
								break;
						
				default: 
					System.out.println("SKILL USE KEY INPUT UNHANDLED CASE, returning to Game");
					game.setGameState(STATE.Game);	
					break;
									
				}
				currentPlayer.canUseSkill = false;
				currentPlayer.setMAUT(false);
				currentMap.nullAttackMenu();
				game.pathGenerator.resetTiles();
				if (currentPlayer.skill.nameEquals("Summoning")) currentPlayer.canUseSkill = true;
				if (!currentPlayer.skill.nameEquals("Divine Blessing") && !currentPlayer.skill.nameEquals("Confession") && !currentPlayer.skill.nameEquals("Condar")) {
					game.setGameState(STATE.Game);
				}
			}
			
		}
		
		
		
		else if (game.gameState == STATE.weaponSelection) {
		
			if (key == 'a') {
				if (game.playerGFX.getSelectedItem().category.equalsIgnoreCase("Healing")) {
					game.setPathGenerator(new PathGenerator(game, currentTile, game.playerGFX.getSelectedItem().range + currentPlayer.staffExtention));
				} else if (game.playerGFX.getSelectedItem().weaponType.equalsIgnoreCase("Bow")){
					game.setPathGenerator(new PathGenerator(game, currentTile, game.playerGFX.getSelectedItem().range + currentPlayer.bowExtention));
				} else {
					game.setPathGenerator(new PathGenerator(game, currentTile, game.playerGFX.getSelectedItem().range));
				}
				
				game.pathGenerator.resetTiles();
				game.pathGenerator.setAllPathableTiles(true);
				game.chapterOrganizer.currentMap.selectedBoxTile = currentTile;
				game.setGameState(STATE.AttackState);
			}
			if (keyCode == KeyEvent.VK_UP) {
				game.playerGFX.incWeaponChooseIndex(-1);
			} else if (keyCode == KeyEvent.VK_DOWN) {
				game.playerGFX.incWeaponChooseIndex(1);
			}
			
			
		}
		else if (game.gameState == STATE.TradeState) {
		
			if (game.tradeMenu == null) {
				if (key == 'a') {
					Player tradeOpp = game.pathGenerator.getTopTile().carrier;
					if (tradeOpp != null) {
						if (tradeOpp.teamID.equalsIgnoreCase("Ally") && !tradeOpp.equals(currentPlayer)) {
								game.setTradeMenu(currentPlayer, tradeOpp);
						} else {
							game.setGameState(STATE.Game);
						}
					} else {
						game.setGameState(STATE.Game);
					}
				if (game.pathGenerator != null) game.pathGenerator.resetTiles();

				}
				moveArrowTile(true);
			} else {
				if (game.tradeMenu.inOtherTradersSpace()) {
					if (keyCode == KeyEvent.VK_UP) {
						game.tradeMenu.incOtherTradingSpaceIndex(-1);
					} else if (keyCode == KeyEvent.VK_DOWN) {
						game.tradeMenu.incOtherTradingSpaceIndex(1);
					}
				} else {
					if (keyCode == KeyEvent.VK_UP) {
						game.tradeMenu.updateSelectedItem(-1);
					} else if (keyCode == KeyEvent.VK_DOWN) {
						game.tradeMenu.updateSelectedItem(1);
					} else if (keyCode == KeyEvent.VK_LEFT) {
						game.tradeMenu.swapTrader();
					} else if (keyCode == KeyEvent.VK_RIGHT) {
						game.tradeMenu.swapTrader();
					}
				}
				if (key == 'a') {
					if (game.tradeMenu.isOnPlayerOne()) {
						
						if (game.tradeMenu.inOtherTradersSpace()) {
							Item first = game.tradeMenu.getOtherTradingSpaceItem();
							Item second = game.tradeMenu.itemToTrade();
							
							if (game.tradeMenu.trader1.swapItem(game.tradeMenu.trader2, second, first)) {
								currentPlayer.canTrade = false;
								currentPlayer.canMove = false;
							}
							game.tradeMenu.updateSelectedItem(0); // this brings us back to the top so we dont trade null
							game.tradeMenu.setInOtherTradingSpace(false);
						} else {
							game.tradeMenu.setInOtherTradingSpace(true);
							game.tradeMenu.incOtherTradingSpaceIndex(25);
						}
					
					}
					else {
					
						if (game.tradeMenu.inOtherTradersSpace()) {
							Item first = game.tradeMenu.getOtherTradingSpaceItem();
							Item second = game.tradeMenu.itemToTrade();
							if (game.tradeMenu.trader1.swapItem(game.tradeMenu.trader2, first, second)) {
								currentPlayer.canTrade = false;
								currentPlayer.canMove = false;
							}
					
							game.tradeMenu.updateSelectedItem(0); // this brings us back to the top so we dont trade null
							game.tradeMenu.setInOtherTradingSpace(false);
						} else {
							game.tradeMenu.setInOtherTradingSpace(true);
							game.tradeMenu.incOtherTradingSpaceIndex(25);
						}
						
					
					}
				}
				
			}
			
		} else if (game.gameState == STATE.TalkState) {
			
			game.pathGenerator.eraseScopes();

			if (key == 'a' ) {
				Tile topTile = game.pathGenerator.getTopTile();
				if (topTile.isOccupied()) {
					Player opponent = topTile.carrier;
					if (opponent.sameTeam(currentPlayer)) return;
					if (opponent.talkedToUnit) {
						game.pathGenerator.resetTiles();
						currentPlayer.setMAUT(false);
						game.setGameState(STATE.Game);
						game.chapterOrganizer.transformEnemyIntoAlly(opponent);
					}
				}
			} else {
				moveAttackTile();
			}
		}
		
		else if (game.gameState == STATE.MoveState) {

			if (currentPlayer != null) moveArrowTile(currentPlayer.isFlier);
			
			if (key == 'a') {
				Tile top = game.pathGenerator.getTopTile();
				if (top.placeEquals(currentTile)) {
					setPopUpMenu();
				} else if (top.pathable && !top.isOccupied()) {
					game.chapterOrganizer.currentMap.move(currentTile.carrier, game.pathGenerator.getTopTile());
					game.chapterOrganizer.currentMap.setCurrentTile(game.pathGenerator.getTopTile());
					game.pathGenerator.resetTiles();
					game.setPopUpMenu(new PopUpMenu(game, currentPlayer.currentTile));
					game.SFX.playSelect();
					game.setGameState(STATE.PopUpMenu);
				}
			}
		} else if (game.gameState == STATE.AttackState) {
			
			currentPlayer = currentTile.carrier;
			if (!currentPlayer.teamID.equalsIgnoreCase("Ally")) {
				if (game.pathGenerator != null) game.pathGenerator.resetTiles();
				game.setGameState(STATE.Game);
			}
			if (currentPlayer == null) System.err.println("idk how this happened, attack state KeyInput currentPlayer is null");
			game.pathGenerator.eraseScopes();
			if (!currentMap.inAttackMenu) {
				moveAttackTile();
			}
			Tile topTile = game.pathGenerator.getTopTile();
			
			if (key == 'a' && topTile.isOccupied()) {
					Player opponent = topTile.carrier;
					if (currentPlayer.teamID.equalsIgnoreCase(opponent.teamID)) { //if opponent and player are on the same team
						if (currentPlayer.isHealer()) { //if we are a healer
							if (currentPlayer.equals(opponent)) return;
							if (currentPlayer.equiptItem.category.equalsIgnoreCase("Healing")) {
								if (opponent.currentHP < opponent.HP) {
									currentPlayer.healPlayer(opponent);
									game.pathGenerator.resetTiles();
									game.startEXPScene(false);
								}
							} else return;
						} else if (currentPlayer.isDancer()) { //a dancer
							if (opponent.equals(currentPlayer)) return;
							
							currentPlayer.danceForPlayer(opponent);
							game.pathGenerator.resetTiles();
							game.startEXPScene(false);
						}
					} else { //opponent is on a different team than player
						//this is where we put in the logic for attacking
						if (currentPlayer.isHealer() && !currentPlayer.duoWeaponHeal) return;
						if (currentPlayer.isDancer()) return;
						if (currentPlayer.equiptItem.category.equalsIgnoreCase("Healing")) return;
						
						if (currentMap.inAttackMenu) {
							game.AttackManager.Attack(currentPlayer, opponent);
							game.pathGenerator.resetTiles();
							game.pathGenerator.eraseScopes();
							currentMap.nullAttackMenu();
							
						} else {
						
							currentMap.setAttackMenu(new AttackMenu(currentPlayer, opponent));
							
						}

					}
			} else if (!topTile.isOccupied()){
				//we click on something that is not occupied
				//could be a breakable wall - we should check
				if (topTile.category.equalsIgnoreCase("DamagedWall")) {
					if (key == 'a') {
						DamagedWallTile dmgWall = (DamagedWallTile) topTile;
						dmgWall.decHealth(currentPlayer.getDamage());
						currentPlayer.setMAUT(false);
						game.pathGenerator.resetTiles();
						game.pathGenerator.eraseScopes();
						game.setGameState(STATE.Game);
					}
				}
			}
			
		} else if (game.gameState == STATE.Menu) {
			
			if (keyCode == KeyEvent.VK_DOWN) {
				game.menu.incSelectedOptions();
			} else if (keyCode == KeyEvent.VK_UP) {
				game.menu.decSelectedOptions();
			}
			
			if (key == 'a') {
				if (game.menu.menuMode == MODE.DifficultySelection) {
					
					game.menu.selectDifficulty();	
					
				} else if (game.menu.menuMode == MODE.Main) {
					if (game.timekeep.sessionAtDesiredTime(0.6)) {
						
						if (game.menu.optionIndex == 0) { // new game!
						
							game.menu.setMenuMode(MODE.DifficultySelection);
										
						} else if (game.menu.optionIndex == 1) {
						
							game.setGameState(STATE.LoadGame); //load game -- only time load level will not be 0!
				
						} else if (game.menu.optionIndex == 2) {
							//choose chapter state
							if (game.inDevMode) {
								game.setChapterChooser();
							} else {
								game.menu.setMenuMode(MODE.DevLogin);
							}
						
						} else if (game.menu.optionIndex == 3) {
							//this is where we enter our Chapter Design state!!!
							if (game.inDevMode) {
								game.setChapterDesigner();
							}
						}
					}
				} else if (game.menu.menuMode == MODE.LoadLevelSelection){
					// create a new game at the given Load Level
					game.menu.createNewGame();
				}
			}
			
			if (game.menu.menuMode == MODE.DevLogin) {
				if (keyCode == KeyEvent.VK_ENTER) {
					// compare passwords, allow access if they match
					game.menu.compare_passwords();
				} else {
					
					if (keyCode == KeyEvent.VK_BACK_SPACE) {
						game.menu.backSpace();
					}
					
					if (Character.isAlphabetic(key) || Character.isDigit(key)) {
						game.menu.addToAttemptedPassword(key);
					}
					
					if (keyCode == KeyEvent.VK_ESCAPE) {
						game.menu.setMenuMode(MODE.Main);
					}
					
				}
			}
			
		} else if (game.gameState == STATE.ChapterChoose) {
			if (keyCode == KeyEvent.VK_RIGHT) {
				game.chapterChooser.incBoxIndex();
			} else if (keyCode == KeyEvent.VK_LEFT) {
				game.chapterChooser.decBoxIndex();
			} else if (keyCode == KeyEvent.VK_DOWN) {
				game.chapterChooser.boxIndex = 6;
			} else if (keyCode == KeyEvent.VK_UP) {
				game.chapterChooser.boxIndex = 0;
			}
			if (key == 'a') {
				if (game.chapterChooser.boxIndex == 6) { //clicks on left arrow
					game.chapterChooser.decScreenNum();
				} else if (game.chapterChooser.boxIndex == 7) { //clicks on right arrow
					game.chapterChooser.incScreenNum();
				} else {
					//clicks on any of the chapter boxes
					game.chapterChooser.chooseGameChapter();
					game.cutScenes.startScene(game.chapterOrganizer.currentChapter, true);
					game.gameState = STATE.outGameCutScene;
				}
			} else if (keyCode == KeyEvent.VK_ESCAPE) {
				game.backToMenu();
			}

		} else if (game.gameState == STATE.ReturnToMenu) {
			if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_UP) {
				if (game.menuYes) game.menuYes = false;
				else game.menuYes = true;
			} else if (key == 'a') {
				if (game.menuYes) {
					game.backToMenu();
				} else {
					game.setGameState(STATE.Game);
				}
			}
		} else if (game.gameState == STATE.LoadGame) {
			
			if (!game.gameLoader.isSaving) {
				if (keyCode == KeyEvent.VK_UP) {
					game.gameLoader.changeSelectedIndex(-1);
				} else if (keyCode == KeyEvent.VK_DOWN) {
					game.gameLoader.changeSelectedIndex(1);
				}  
			}
			
			if (key == 'a') {
				if (!game.gameLoader.isSaving) game.gameLoader.loadGame();
				else {
					if (game.chapterOrganizer.currentChapter == 8) {
						game.chapterOrganizer.receiveArmoryGold();
					}
					game.gameLoader.saveGame();
				}
				game.cutScenes.startScene(game.chapterOrganizer.currentChapter, true);
				game.gameState = STATE.outGameCutScene;
			}
			
		} else if (game.gameState == STATE.Armory ) {

			if (!game.armoryHandler.inItemPurchaseMode()) {
				if (keyCode == KeyEvent.VK_UP) {
					game.armoryHandler.incSelectedIndex(-1);
				} else if (keyCode == KeyEvent.VK_DOWN) {
					game.armoryHandler.incSelectedIndex(1);				
				}
				
				if (key == 'a') {
					game.armoryHandler.setItemPurchaseSelection();
				}
				
			} else {
				if (key == 'a') {
					game.armoryHandler.sellItem();

				} else {
					game.armoryHandler.setItemPuchaseMode(false);
				}
			}
			
			
			
		} else if (game.gameState == STATE.BattlePreparations) {
			
			
			if (game.chapterOrganizer.battlePrep.prepState == PreparationState.AllySelection) {
				if (keyCode == KeyEvent.VK_LEFT) {
					game.chapterOrganizer.battlePrep.incSelectedIndex(-1);
				} else if (keyCode == KeyEvent.VK_RIGHT) {
					game.chapterOrganizer.battlePrep.incSelectedIndex(1);				
				} else if (keyCode == KeyEvent.VK_UP) {
					game.chapterOrganizer.battlePrep.incSelectedIndex(-2);
				} else if (keyCode == KeyEvent.VK_DOWN) {
					game.chapterOrganizer.battlePrep.incSelectedIndex(2);				
				}
			
				if (key == 'a') {
					game.chapterOrganizer.battlePrep.flipDeployed();
				}
			
			} else if (game.chapterOrganizer.battlePrep.prepState == PreparationState.MainMenu) {
				
				if (keyCode == KeyEvent.VK_UP) {
					game.chapterOrganizer.battlePrep.incSelectedIndex(-1);
				} else if (keyCode == KeyEvent.VK_DOWN) {
					game.chapterOrganizer.battlePrep.incSelectedIndex(1);				
				}
			
				if (key == 'a') {
					game.chapterOrganizer.battlePrep.chooseStateFromMenu();
				}
				
			} else if (game.chapterOrganizer.battlePrep.prepState == PreparationState.AllyPositions) {
				
				moveCurrentTile();
				
				if (key == 'a') {
					if (game.chapterOrganizer.battlePrep.hasSelectedPlayer()) {
						
						game.chapterOrganizer.battlePrep.swapWithPlayer(currentTile.carrier);
						game.chapterOrganizer.battlePrep.setSelectedPlayer(null);
					} else {
						
						if (currentMap.currentTile.isOccupied()) {
							if (currentMap.currentTile.carrier.teamID.equalsIgnoreCase("Ally")) {
								game.chapterOrganizer.battlePrep.setSelectedPlayer(currentMap.currentTile.carrier);
							}
						}
						
					}
				}
				
			} else if (game.chapterOrganizer.battlePrep.prepState == PreparationState.AllyInfo) {
				
				if (game.tradeMenu == null) {
				
					if (keyCode == KeyEvent.VK_RIGHT) {
						game.chapterOrganizer.battlePrep.incSelectedIndex(1);
						game.chapterOrganizer.battlePrep.changeTrader();
					} else if (keyCode == KeyEvent.VK_LEFT) {
						game.chapterOrganizer.battlePrep.incSelectedIndex(-1);
						game.chapterOrganizer.battlePrep.changeTrader();
					} else if (keyCode == KeyEvent.VK_UP) {
						game.chapterOrganizer.battlePrep.incSelectedIndex(-1*game.chapterOrganizer.battlePrep.nCols());
						game.chapterOrganizer.battlePrep.changeTrader();
					} else if (keyCode == KeyEvent.VK_DOWN) {
						game.chapterOrganizer.battlePrep.incSelectedIndex(game.chapterOrganizer.battlePrep.nCols());				
						game.chapterOrganizer.battlePrep.changeTrader();
					}
				
					if (key == 'a') {
						game.chapterOrganizer.battlePrep.selectTrader();
					}
				} else {
					//we are currently trading
					if (keyCode == KeyEvent.VK_UP) {
						game.tradeMenu.updateSelectedItem(-1);
					} else if (keyCode == KeyEvent.VK_DOWN) {
						game.tradeMenu.updateSelectedItem(1);
					} else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
						game.tradeMenu.swapTrader();
					}
					if (key == 'a') {
						if (game.tradeMenu.isOnPlayerOne()) {
							if (game.tradeMenu.trader2 != null) {
								game.tradeMenu.trader1.giveItem(game.tradeMenu.trader2, game.tradeMenu.itemToTrade());
								game.tradeMenu.updateSelectedItem(0); // prevents null pointer exception by bringing to top
							} else {
								game.tradeMenu.trader1.giveItem(game.tradeMenu.getConvoy(), game.tradeMenu.itemToTrade());
								game.tradeMenu.updateSelectedItem(0); // prevents null pointer exception by bringing to top
							}
						}
						else {
							if (game.tradeMenu.trader2 != null) {
								game.tradeMenu.trader2.giveItem(game.tradeMenu.trader1, game.tradeMenu.itemToTrade());
								game.tradeMenu.updateSelectedItem(0); // this brings us back to the top so we dont trade null
							} else {
								game.tradeMenu.getConvoy().giveItem(game.tradeMenu.trader1, game.tradeMenu.itemToTrade());
								game.tradeMenu.updateSelectedItem(0); // this brings us back to the top so we dont trade null
							}
						}
					}
				}
			}
			
		} else if (game.gameState == STATE.outGameCutScene) {
			
			if (keyCode == KeyEvent.VK_LEFT) {
				game.cutScenes.cutScene.setPreviousLine();
			} else if (keyCode == KeyEvent.VK_ENTER) {
				game.cutScenes.cutScene.setNextLine();
			} else {
				game.cutScenes.cutScene.fillLine();
			}
		
		} else if (game.gameState == STATE.inGameCutScene) {
			
			if (keyCode == KeyEvent.VK_LEFT) {
				game.cutScenes.inGameCutScene.setPreviousLine();
			} else if (keyCode == KeyEvent.VK_ENTER) {
				game.cutScenes.inGameCutScene.setNextLine();
			} else {
				game.cutScenes.inGameCutScene.fillLine();
			}
			
		}
		else if (game.gameState == STATE.keyOpeningState) {
			moveArrowTile(true);
			
			if (key == 'a') {
				game.pathGenerator.resetTiles();
				Tile top = currentMap.selectedBoxTile;
				if (top.category.equalsIgnoreCase("Door") || top.category.equalsIgnoreCase("Chest")) {
					
					currentMap.setTile(top, new FloorTile(top.x, top.y, currentMap), 0);
					if (currentMap.selectedKey != null) currentMap.selectedKey.carrier.decItemDuration(currentMap.selectedKey);
					if (currentPlayer != null) currentPlayer.setMAU(false);
					if (top.category.equalsIgnoreCase("Chest")) {
						ChestTile chest = (ChestTile) top;
						chest.giveGift(currentPlayer);
						return;
					}
				}
				game.setGameState(STATE.Game);
			}
		} else if (game.gameState == STATE.optionsMenu) {
			
			if (game.optionsMenu == null) return;
			
			if (keyCode == KeyEvent.VK_DOWN) {
				game.optionsMenu.incSelectedOption(1);
			} else if (keyCode == KeyEvent.VK_UP) {
				game.optionsMenu.incSelectedOption(-1);
			} else if (keyCode == KeyEvent.VK_RIGHT) {
				game.optionsMenu.incHorizSelection(1);
			} else if (keyCode == KeyEvent.VK_LEFT) {
				game.optionsMenu.incHorizSelection(-1);
			}
			if (key == 'a') {
				game.optionsMenu.select();
			}
			
		}
		
	}
	
	
	
	
	/** responsible for moving the cursor around the map*/
	public void moveCurrentTile() {
		if (keyCode == KeyEvent.VK_RIGHT) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x + 1, currentTile.y));
			game.SFX.playCursor();
		} else if (keyCode == KeyEvent.VK_LEFT) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x - 1, currentTile.y));
			game.SFX.playCursor();
		} else if (keyCode == KeyEvent.VK_UP) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x, currentTile.y - 1));
			game.SFX.playCursor();
		} else if (keyCode == KeyEvent.VK_DOWN) {
			game.chapterOrganizer.currentMap.setCurrentTile(
					game.chapterOrganizer.currentMap.getTileAtAbsolutePos(currentTile.x, currentTile.y + 1));
			game.SFX.playCursor();
		}
	
	}
	/**for movestates or any state that draws a long trace */
	private void moveArrowTile(boolean isFlier) {
		Tile arrowTile;
		if (keyCode == KeyEvent.VK_RIGHT) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x + 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.setTileArrow(arrowTile, isFlier);
		} else if (keyCode == KeyEvent.VK_LEFT) {			
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x - 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.setTileArrow(arrowTile, isFlier);
		} else if (keyCode == KeyEvent.VK_UP) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y - 1);
			game.pathGenerator.setTileArrow(arrowTile, isFlier);
		} else if (keyCode == KeyEvent.VK_DOWN) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y + 1);
			game.pathGenerator.setTileArrow(arrowTile, isFlier);
		}
		game.SFX.playCursor();
	}
	
	/** For attack states */
	private void moveAttackTile() {
		if (game.pathGenerator == null) return;
		if (game.chapterOrganizer == null) return;
		if (game.chapterOrganizer.currentMap == null) return;
		Tile arrowTile;
		if (keyCode == KeyEvent.VK_RIGHT) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x + 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.drawTilePath(arrowTile);
		} else if (keyCode == KeyEvent.VK_LEFT) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x - 1, game.pathGenerator.getTopTile().y);
			game.pathGenerator.drawTilePath(arrowTile);
		} else if (keyCode == KeyEvent.VK_UP) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y - 1);
			game.pathGenerator.drawTilePath(arrowTile);
		} else if (keyCode == KeyEvent.VK_DOWN) {
			arrowTile = game.chapterOrganizer.currentMap.getTileAtAbsolutePos(game.pathGenerator.getTopTile().x, game.pathGenerator.getTopTile().y + 1);
			game.pathGenerator.drawTilePath(arrowTile);
		}
		game.SFX.playCursor();
	}
	
	public void setPopUpMenu() {
		game.setPopUpMenu(new PopUpMenu(game, currentTile));
		if (game.pathGenerator != null) game.pathGenerator.resetTiles();
		game.SFX.playSelect();
		game.setGameState(STATE.PopUpMenu);
	}
}
