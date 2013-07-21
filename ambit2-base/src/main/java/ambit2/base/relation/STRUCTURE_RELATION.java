package ambit2.base.relation;


public enum STRUCTURE_RELATION {
	SIMILARITY,
	HAS_TAUTOMER {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return TAUTOMER_OF;
		}
	},
	HAS_METABOLITE {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return METABOLITE_OF;
		}
	},
	HAS_CONSTITUENT {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return CONSTITUENT_OF;
		}
	},
	HAS_ADDITIVE {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return ADDITIVE_OF;
		}
	},	
	HAS_IMPURITY {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return IMPURITY_OF;
		}
	}	
	TAUTOMER_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_TAUTOMER;
		}
	},
	METABOLITE_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_METABOLITE;
		}
	},
	CONSTITUENT_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_CONSTITUENT;
		}
	},	
	ADDITIVE_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_ADDITIVE;
		}
	},
	IMPURITY_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_IMPURITY;
		}
	};			
	public STRUCTURE_RELATION inverseOf() {
		return null;
	}
}

