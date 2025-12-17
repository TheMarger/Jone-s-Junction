
class game {
	int currentLevel;
	protected game[] savedGames;
	
		protected void game(int currentLevel) {
			this.currentLevel = currentLevel;
			//Level level = new Level(currentLevel);
			boolean isSpeedRun;
		}
		
		protected int getCurrentLevel() {
			return currentLevel;
		}
		protected void setCurrentLevel(int level) {
			this.currentLevel = level;
		}
		protected game[] getSavedGames() {
			return savedGames;
		}
		protected void setSavedGames(game[] savedGames) {
			this.savedGames = savedGames;
		}

}
