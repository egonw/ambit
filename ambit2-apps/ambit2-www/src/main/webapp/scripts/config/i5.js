var config_i5 = {
		        "GI_GENERAL_INFORM_SECTION": {
		            "conditions": {
		                "remark": {
		                    "iOrder": -4
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "sTitle": "Physical state",
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "result": {
		                    "bVisible": false
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Substance type",
		                    "iOrder": -3,
		                    "inMatrix": true
		                }
		            }
		        },
		        "PC_MELTING_SECTION": {
		            "conditions": {
		                "decomposition": {
		                    "iOrder": -2
		                },
		                "sublimation": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -3,
		                    "inMatrix": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "PC_BOILING_SECTION": {
		            "conditions": {
		                "atm. pressure": {
		                    "sTitle": "Pressure",
		                    "inMatrix": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -1,
		                    "inMatrix": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "PC_VAPOUR_SECTION": {
		            "conditions": {
		                "temperature": {
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "decomposition": {
		                    "bVisible": false
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -3,
		                    "inMatrix": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "PC_PARTITION_SECTION": {
		            "parameters": {
		                "method type": {
		                    "bVisible": false
		                }
		            },
		            "conditions": {
		                "temperature": {
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "ph": {
		                    "sTitle": "pH",
		                    "iOrder": -1,
		                    "inMatrix": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -3,
		                    "inMatrix": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "PC_WATER_SOL_SECTION": {
		            "parameters": {
		                "method type": {
		                    "bVisible": false
		                }
		            },
		            "conditions": {
		                "temperature": {
		                    "iOrder": -3,
		                    "inMatrix": true
		                },
		                "ph": {
		                    "sTitle": "pH",
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "remark": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -4,
		                    "inMatrix": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "PC_SOL_ORGANIC_SECTION": {
		            "conditions": {
		                "solvent": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                },
		                "temperature": {
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "remark": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "Solubility in solv",
		                    "inMatrix": true,
		                    "iOrder": -3
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "PC_NON_SATURATED_PH_SECTION": {
		            "parameters": {
		                "method type": {
		                    "bVisible": false
		                }
		            },
		            "conditions": {
		                "temperature": {
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "doses/concentrations": {
		                    "sTitle": "Concentration",
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "pH Value",
		                    "inMatrix": true,
		                    "iOrder": -3
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "PC_DISSOCIATION_SECTION": {
		            "conditions": {
		                "no": {
		                    "sTitle": "pKa No.",
		                    "iOrder": -3
		                },
		                "temperature": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "pKa Value",
		                    "inMatrix": true,
		                    "iOrder": -5
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "PC_UNKNOWN_SECTION": {
		            "effects": {
		                "endpoint": {
		                    "inMatrix": true,
		                    "bVisible": true
		                },
		                "result": {
		                    "inMatrix": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": true,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                	"bVisible": true,
		                    "inMatrix": true
		                }
		            }
		        },
		        "TO_ACUTE_ORAL_SECTION": {
		            "parameters": {
		                "species": {
		                	"comment" : "Moved to conditions",
		                    "iOrder": -7,
		                    "inMatrix": false,
		                    "bVisible" : false
		                }
		            },
		            "conditions": {
		                "sex": {
		                    "iOrder": -4
		                },
		                "species": {
		                    "iOrder": -7,
		                    "inMatrix": true
		                }                
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -6,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -5,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -1
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "iOrder": -3,
		                    "sTitle": "Interpretation of the results"
		                },
		                "criteria": {
		                    "iOrder": -2
		                }
		            }
		        },
		        "TO_ACUTE_DERMAL_SECTION": {
		            "parameters": {
		                "species": {
		                	"comment" : "Moved to conditions",
		                    "iOrder": -7,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },            	
		                
		                "sex": {
		                    "iOrder": -4
		                }
		            },
		            "conditions": {
		                "sex": {
		                    "iOrder": -4
		                },
		                "species": {
		                    "iOrder": -7,
		                    "inMatrix": true
		                }                
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -6,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -5,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -1
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "iOrder": -3,
		                    "sTitle": "Interpretation of the results"
		                },
		                "criteria": {
		                    "iOrder": -2
		                }
		            }
		        },
		        "TO_ACUTE_INHAL_SECTION": {
		            "parameters": {
		                "species": {
		                	"comment" : "Moved to conditions",
		                    "iOrder": -7,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },                   
		                "sex": {
		                    "iOrder": -4
		                }
		            },
		            "conditions": {
		                "species": {
		                    "iOrder": -8,
		                    "inMatrix": true
		                },            	
		                "sex": {
		                    "iOrder": -4
		                },
		                "route of administration": {
		                    "iOrder": -7
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -6,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -5,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -1
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "iOrder": -3,
		                    "sTitle": "Interpretation of the results"
		                },
		                "criteria": {
		                    "iOrder": -2
		                }
		            }
		        },
		        "TO_SKIN_IRRITATION_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -9,
		                    "sTitle": "Method type",
		                    "inMatrix": true
		                },
		                "species": {
		                    "iOrder": -8
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -3
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "bVisible": false
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Interpretation of the results",
		                    "iOrder": -7,
		                    "inMatrix": true
		                },
		                "criteria": {
		                    "iOrder": -6
		                }
		            }
		        },
		        "TO_EYE_IRRITATION_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -7,
		                    "sTitle": "Method type",
		                    "inMatrix": true
		                },
		                "species": {
		                    "iOrder": -6
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -1
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "bVisible": false
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Interpretation of the results",
		                    "iOrder": -3,
		                    "inMatrix": true
		                },
		                "criteria": {
		                    "iOrder": -2
		                }
		            }
		        },
		        "TO_SENSITIZATION_SECTION": {
		            "parameters": {
		                "species": {
		                    "iOrder": -6,
		                    "bVisible": false
		                },
		                "type of study": {
		                    "iOrder": -11,
		                    "sTitle": "Study type",
		                    "inMatrix": true
		                },
		                "type of method": {
		                    "iOrder": -8,
		                    "sTitle": "Method type"
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -3
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -8,
		                    "bVisible": false
		                },
		                "result": {
		                    "iOrder": -7,
		                    "bVisible": false
		                },
		                "text": {
		                    "sTitle": "",
		                    "bVisible": false,
		                    "iOrder": -6
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Interpretation of the results",
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "criteria": {
		                    "iOrder": -9
		                }
		            }
		        },
		        "TO_REPEATED_ORAL_SECTION": {
		            "parameters": {
		                "species": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -10,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },
		                "test type": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -9,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },
		                "route of administration": {
		                    "iOrder": -8
		                },
		                "doses/concentrations": {
		                    "sTitle": "Dose/concentrations",
		                    "iOrder": -7
		                }
		            },
		            "conditions": {
		                "test type": {
		                    "iOrder": -9,
		                    "inMatrix": true
		                },
		            	"species": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },            	
		                "sex": {
		                    "iOrder": -4
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -6,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -5,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -3
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "TO_REPEATED_INHAL_SECTION": {
		            "parameters": {
		                "species": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -10,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },            	
		                "test type": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -9,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },                
		                "route of administration": {
		                    "iOrder": -8
		                },
		                "doses/concentrations": {
		                    "sTitle": "Dose/concentrations",
		                    "iOrder": -7
		                }
		            },
		            "conditions": {
		                "test type": {
		                    "iOrder": -9,
		                    "inMatrix": true
		                },            	
		                "species": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		            	
		                "sex": {
		                    "iOrder": -4
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -6,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -5,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -3
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "TO_REPEATED_DERMAL_SECTION": {
		            "parameters": {
		                "species": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -10,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },            	
		                "type_coverage": {
		                    "iOrder": -10,
		                    "sTitle": "Type of coverage"
		                },
		                "route of administration": {
		                    "iOrder": -8
		                },
		                "test type": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -9,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },                
		                "doses/concentrations": {
		                    "sTitle": "Dose/concentrations",
		                    "iOrder": -7
		                }
		            },
		            "conditions": {
		                "test type": {
		                    "iOrder": -9,
		                    "inMatrix": true
		                },                
		            	
		                "species": {
		                    "iOrder": -11,
		                    "inMatrix" : true
		                },
		            	
		                "sex": {
		                    "iOrder": -4
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -6,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -5,
		                    "inMatrix": true
		                }                
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -3
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "TO_GENETIC_IN_VITRO_SECTION": {
		            "parameters": {
		                "type of genotoxicity": {
		                    "iOrder": -9,
		                    "sTitle": "Genotoxicity type"
		                },
		                "type of study": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -8,
		                    "sTitle": "Study type",
		                    "inMatrix": false,
		                    "bVisible" : false
		                },
		                "metabolic activation system": {
		                    "iOrder": -7,
		                    "bVisible": true
		                },
		                "target gene": {
		                    "iOrder": -6
		                }
		            },
		            "conditions": {
		                "type of study": {
		                    "iOrder": -8,
		                    "sTitle": "Study type",
		                    "inMatrix": true,
		                    "bVisible" : true
		                },            	
		                "metabolic activation system": {
		                    "iOrder": -4,
		                    "sTitle": "Metabolic activation"
		                },
		                "metabolic activation": {
		                    "iOrder": -4
		                },
		                "species": {
		                    "iOrder": -5,
		                    "sTitle": "Species/strain"
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "bVisible": false,
		                    "sTitle": "Genotoxicity",
		                    "iOrder": -3
		                },
		                "text": {
		                    "sTitle": "Genotoxicity",
		                    "bVisible": true,
		                    "iOrder": -3
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -1,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Interpretation of the result",
		                    "bVisible": true,
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "criteria": {
		                    "bVisible": false
		                }
		            }
		        },
		        "TO_GENETIC_IN_VIVO_SECTION": {
		            "parameters": {
		                "type of study": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -8,
		                    "sTitle": "Study type",
		                    "inMatrix": false,
		                    "bVisible" : false
		                },            	
		                "type of genotoxicity": {
		                    "iOrder": -9,
		                    "sTitle": "Genotoxicity type"
		                },
		                "route of administration": {
		                    "iOrder": -7,
		                    "bVisible": true
		                },
		                "species": {
		                    "iOrder": -6
		                }
		            },
		            "conditions": {
		                "type of study": {
		                    "iOrder": -8,
		                    "sTitle": "Study type",
		                    "inMatrix": true
		                },            	
		                "sex": {
		                    "iOrder": -5
		                },
		                "toxicity": {
		                    "iOrder": -4
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "bVisible": false,
		                    "sTitle": "Genotoxicity",
		                    "iOrder": -3
		                },
		                "text": {
		                    "sTitle": "Genotoxicity",
		                    "bVisible": true,
		                    "iOrder": -3
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -1,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Interpretation of the result",
		                    "bVisible": true,
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "criteria": {
		                    "bVisible": false
		                }
		            }
		        },
		        "TO_CARCINOGENICITY_SECTION": {
		            "parameters": {
		                "species": {
		                	"comment"  :"copied to conditions",
		                    "iOrder": -10,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },
		                "route of administration": {
		                    "iOrder": -9
		                },
		                "doses/concentrations": {
		                    "iOrder": -8,
		                    "sTitle": "Dose/concentrations"
		                },
		                "type of genotoxicity": {
		                    "bVisible": false
		                },
		                "type of study": {
		                    "bVisible": false
		                }
		            },
		            "conditions": {
		                "species": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "effect type": {
		                    "iOrder": -6,
		                    "inMatrix": true
		                },
		                "sex": {
		                    "bVisible": false
		                },
		                "toxicity": {
		                    "bVisible": false
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -7,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -5,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -1
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "iOrder": -3,
		                    "bVisible": false,
		                    "sTitle": "Interpretation of the results"
		                },
		                "criteria": {
		                    "bVisible": false,
		                    "iOrder": -2
		                }
		            }
		        },
		        "TO_REPRODUCTION_SECTION": {
		            "parameters": {
		                "species": {
		                	"comment"  :"copied to conditions",
		                    "iOrder": -10,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },                
		                "route of administration": {
		                    "iOrder": -9
		                },
		                "doses/concentrations": {
		                    "sTitle": "Dose/concentrations",
		                    "iOrder": -8
		                }
		            },
		            "conditions": {
		                "species": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "generation": {
		                    "iOrder": -7,
		                    "inMatrix": true
		                },
		                "sex": {
		                    "iOrder": -4
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -6,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -5,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -3,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "TO_DEVELOPMENTAL_SECTION": {
		            "parameters": {
		                "species": {
		                	"comment"  :"copied to conditions",
		                    "iOrder": -10,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },   
		                
		                "route of administration": {
		                    "iOrder": -9
		                },
		                "doses/concentrations": {
		                    "sTitle": "Dose/concentrations",
		                    "iOrder": -8
		                }
		            },
		            "conditions": {
		                "species": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },            	
		                "sex": {
		                    "iOrder": -4
		                },
		                "effect type": {
		                    "iOrder": -7,
		                    "inMatrix": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -6,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -5,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -3,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "TO_PHOTOTRANS_AIR_SECTION": {
		            "parameters": {
		                "reactant": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -9,
		                    "inMatrix": false,
		                    "bVisible" : false
		                }
		            },
		            "conditions": {
		                "reactant": {
		                    "iOrder": -9,
		                    "inMatrix": true
		                },            	
		                "test condition": {
		                    "iOrder": -8,
		                    "inMatrix": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "DT50",
		                    "inMatrix": true,
		                    "iOrder": -7
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "TO_HYDROLYSIS_SECTION": {
		            "conditions": {
		                "ph": {
		                    "iOrder": -3,
		                    "inMatrix": true
		                },
		                "temperature": {
		                    "iOrder": -2,
		                    "inMatrix": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "DT50",
		                    "inMatrix": true,
		                    "iOrder": -1
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "TO_BIODEG_WATER_SCREEN_SECTION": {
		            "parameters": {
		                "test type": {
		                    "iOrder": -4
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": false
		                }
		            },
		            "conditions": {
		                "sampling time": {
		                    "iOrder": -3
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "sTitle": "Degrad. Parameter",
		                    "iOrder": -1,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Degradation",
		                    "iOrder": -2,
		                    "inMatrix": true
		                }
		            },
		            "interpretation": {
		                "criteria": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "Result",
		                    "inMatrix": true
		                }
		            }
		        },
		        "TO_BIODEG_WATER_SIM_SECTION": {
		            "parameters": {
		                "test type": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -9,
		                    "inMatrix": false,
		                    "bVisible" : false
		                }
		            },
		            "conditions": {
		                "test type": {
		                    "iOrder": -9,
		                    "inMatrix": true,
		                    "bVisible" : true
		                },            	
		                "sampling time": {
		                    "iOrder": -8
		                },
		                "degradation": {
		                    "iOrder": -7
		                },
		                "degradation parameter": {
		                    "iOrder": -6
		                },
		                "compartment": {
		                    "iOrder": -5
		                }
		            },
		            "effects": {
		                "test type": {
		                    "iOrder": -9,
		                    "inMatrix": true
		                },            	
		                "endpoint": {
		                    "sTitle": "Half-life",
		                    "inMatrix": true,
		                    "iOrder": -4
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -3
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EN_STABILITY_IN_SOIL_SECTION": {
		            "parameters": {
		                "test type": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -9,
		                    "inMatrix": false,
		                    "bVisible" : false
		                }
		            },
		            "conditions": {
		                "test type": {
		                    "iOrder": -9,
		                    "inMatrix": true,
		                    "bVisible" : true
		                },
		                "soil no.": {
		                    "iOrder": -8
		                },
		                "soil type": {
		                    "iOrder": -7
		                },
		                "oc content": {
		                    "sTitle": "OC content",
		                    "iOrder": -6
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "sTitle": "Half-life",
		                    "inMatrix": true,
		                    "iOrder": -5
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -4
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EN_BIOACCUMULATION_SECTION": {
		            "parameters": {
		                "route": {
		                	"comment" : "copied to conditions",
		                    "iOrder": -5,
		                    "inMatrix": false,
		                    "bVisible" : false
		                },
		                "species": {
		                    "iOrder": -3,
		                    "bVisible" : true,
		                    "inMatrix": false
		                }
		            },
		            "conditions": {
		                "route": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "doses/concentrations": {
		                    "sTitle": "Conc. / Dose",
		                    "iOrder": -4
		                },
		                "bioacc. basis": {
		                    "iOrder": -2
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "sTitle": "Bioacc. type",
		                    "inMatrix": true,
		                    "iOrder": -1
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": 0
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EN_BIOACCU_TERR_SECTION": {
		            "parameters": {
		                "species": {
		                    "iOrder": -4,
		                    "bVisible": true
		                }
		            },
		            "conditions": {
		                "doses/concentrations": {
		                    "iOrder": -5,
		                    "sTitle": "Conc./Dose",
		                    "bVisible": false
		                },
		                "bioacc. basis": {
		                    "iOrder": -3
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "sTitle": "Bioacc. type",
		                    "inMatrix": true,
		                    "iOrder": -2
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -1
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EN_ADSORPTION_SECTION": {
		            "conditions": {
		                "remark": {
		                    "iOrder": -1
		                },
		                "% org.carbon": {
		                    "bVisible": false
		                },
		                "temperature": {
		                    "bVisible": false
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "sTitle": "Kp type",
		                    "inMatrix": true,
		                    "iOrder": -3
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -2
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EN_HENRY_LAW_SECTION": {
		            "conditions": {
		                "temperature": {
		                    "iOrder": -4
		                },
		                "pressure": {
		                    "iOrder": -3
		                },
		                "remark": {
		                    "iOrder": -2
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "inMatrix": true,
		                    "iOrder": -5
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EC_FISHTOX_SECTION": {
		            "parameters": {
		                "test medium": {
		                    "iOrder": -8
		                },
		                "test organism": {
		                    "iOrder": -7,
		                    "sTitle": "Organism"
		                }
		            },
		            "conditions": {
		                "exposure": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "effect": {
		                    "iOrder": -2
		                },
		                "measured concentration": {
		                    "iOrder": -6,
		                    "sTitle": "Meas. Conc."
		                },
		                "based on": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -3
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EC_CHRONFISHTOX_SECTION": {
		            "parameters": {
		                "test medium": {
		                    "iOrder": -8
		                },
		                "test organism": {
		                    "iOrder": -7,
		                    "sTitle": "Organism"
		                }
		            },
		            "conditions": {
		                "exposure": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "effect": {
		                    "iOrder": -2
		                },
		                "measured concentration": {
		                    "iOrder": -6,
		                    "sTitle": "Meas. Conc."
		                },
		                "based on": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -3
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EC_DAPHNIATOX_SECTION": {
		            "parameters": {
		                "test medium": {
		                    "iOrder": -8
		                },
		                "test organism": {
		                    "iOrder": -7,
		                    "sTitle": "Organism"
		                }
		            },
		            "conditions": {
		                "exposure": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "effect": {
		                    "iOrder": -2
		                },
		                "measured concentration": {
		                    "iOrder": -6,
		                    "sTitle": "Meas. Conc."
		                },
		                "based on": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -3
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EC_CHRONDAPHNIATOX_SECTION": {
		            "parameters": {
		                "test medium": {
		                    "iOrder": -8
		                },
		                "test organism": {
		                    "iOrder": -7,
		                    "sTitle": "Organism"
		                }
		            },
		            "conditions": {
		                "exposure": {
		                    "iOrder": -5
		                },
		                "effect": {
		                    "iOrder": -2
		                },
		                "measured concentration": {
		                    "iOrder": -6,
		                    "sTitle": "Meas. Conc."
		                },
		                "based on": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "",
		                    "iOrder": -3,
		                    "inMatrix": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EC_ALGAETOX_SECTION": {
		            "parameters": {
		                "test medium": {
		                    "iOrder": -8
		                },
		                "test organism": {
		                    "iOrder": -7,
		                    "sTitle": "Organism"
		                }
		            },
		            "conditions": {
		                "exposure": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "effect": {
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "measured concentration": {
		                    "iOrder": -6,
		                    "sTitle": "Meas. Conc."
		                },
		                "based on": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -3
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EC_BACTOX_SECTION": {
		            "parameters": {
		                "test medium": {
		                    "iOrder": -8
		                },
		                "test organism": {
		                    "iOrder": -7,
		                    "sTitle": "Organism"
		                }
		            },
		            "conditions": {
		                "exposure": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "effect": {
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "measured concentration": {
		                    "iOrder": -6,
		                    "sTitle": "Meas. Conc."
		                },
		                "based on": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "",
		                    "iOrder": -3,
		                    "inMatrix": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EC_SOIL_MICRO_TOX_SECTION": {
		            "parameters": {
		                "test medium": {
		                    "iOrder": -8
		                },
		                "test organism": {
		                    "iOrder": -7,
		                    "sTitle": "Organism"
		                }
		            },
		            "conditions": {
		                "exposure": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "effect": {
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "measured concentration": {
		                    "iOrder": -6,
		                    "sTitle": "Meas. Conc."
		                },
		                "based on": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -3
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EC_PLANTTOX_SECTION": {
		            "parameters": {
		                "test medium": {
		                    "iOrder": -9
		                }
		            },
		            "conditions": {
		                "test organism": {
		                    "iOrder": -8,
		                    "sTitle": "Organism"
		                },
		                "exposure": {
		                    "iOrder": -6,
		                    "inMatrix": true
		                },
		                "effect": {
		                    "iOrder": -3,
		                    "inMatrix": true
		                },
		                "measured concentration": {
		                    "iOrder": -7,
		                    "sTitle": "Meas. Conc."
		                },
		                "based on": {
		                    "iOrder": -2
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -4
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EC_SEDIMENTDWELLINGTOX_SECTION": {
		            "parameters": {
		                "test medium": {
		                    "iOrder": -8
		                },
		                "test organism": {
		                    "iOrder": -7,
		                    "sTitle": "Organism"
		                }
		            },
		            "conditions": {
		                "exposure": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "effect": {
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "measured concentration": {
		                    "iOrder": -6,
		                    "sTitle": "Meas. Conc."
		                },
		                "based on": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "",
		                    "iOrder": -3,
		                    "inMatrix": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EC_SOILDWELLINGTOX_SECTION": {
		            "parameters": {
		                "test medium": {
		                    "iOrder": -8
		                },
		                "test organism": {
		                    "iOrder": -7,
		                    "sTitle": "Organism"
		                }
		            },
		            "conditions": {
		                "exposure": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "effect": {
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "measured concentration": {
		                    "iOrder": -6,
		                    "sTitle": "Meas. Conc."
		                },
		                "based on": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -3
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "EC_HONEYBEESTOX_SECTION": {
		            "parameters": {
		                "test medium": {
		                    "iOrder": -8
		                },
		                "test organism": {
		                    "iOrder": -7,
		                    "sTitle": "Organism"
		                }
		            },
		            "conditions": {
		                "exposure": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "effect": {
		                    "iOrder": -2,
		                    "inMatrix": true
		                },
		                "measured concentration": {
		                    "iOrder": -6,
		                    "sTitle": "Meas. Conc."
		                },
		                "based on": {
		                    "iOrder": -1
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -4,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "",
		                    "inMatrix": true,
		                    "iOrder": -3
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "bVisible": false
		                }
		            }
		        },
		        "AGGLOMERATION_AGGREGATION_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -22
		                },
		                "method details": {
		                    "iOrder": -11,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "iOrder": -9,
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -8,
		                    "bVisible": false
		                }
		            },
		            "conditions": {
		                "percentile": {
		                    "bVisible": false
		                },
		                "remark": {
		                    "sTitle": "Remarks",
		                    "iOrder": -15,
		                    "bVisible": false
		                },
		                "medium": {
		                    "sTitle": "Medium",
		                    "iOrder": -16,
		                    "inMatrix": true
		                },
		                "ph": {
		                    "iOrder": -17
		                },
		                "seq_num": {
		                    "iOrder": -21,
		                    "sTitle": "Seq. num."
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -20,
		                    "inMatrix": true
		                },
		                "result": {
		                    "iOrder": -19,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "sTitle": "Reference",
		                    "iOrder": -11
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "iOrder": -7,
		                    "bVisible": false
		                }
		            }
		        },
		        "ASPECT_RATIO_SHAPE_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -22
		                },
		                "method details": {
		                    "iOrder": -11,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "iOrder": -9,
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -8,
		                    "bVisible": false
		                }
		            },
		            "conditions": {
		                "shape_descriptive": {
		                    "sTitle": "Shape",
		                    "iOrder": -19,
		                    "inMatrix": true
		                },
		                "x": {
		                    "iOrder": -16,
		                    "inMatrix": true
		                },
		                "y": {
		                    "iOrder": -15,
		                    "inMatrix": true
		                },
		                "z": {
		                    "iOrder": -14,
		                    "inMatrix": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -20,
		                    "inMatrix": true
		                },
		                "result": {
		                    "iOrder": -18,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "sTitle": "Reference",
		                    "iOrder": -11
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "bVisible": false
		                }
		            }
		        },
		        "ZETA_POTENTIAL_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -22
		                },
		                "method details": {
		                    "iOrder": -10,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "iOrder": -8,
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -7,
		                    "bVisible": false
		                },
		                "testmat_form": {
		                    "sTitle": "Test Material Form",
		                    "bVisible": true,
		                    "iOrder": -21
		                }
		            },
		            "conditions": {
		                "medium": {
		                    "sTitle": "Medium",
		                    "iOrder": -15,
		                    "bVisible": true,
		                    "inMatrix": true
		                },
		                "n": {
		                    "iOrder": -17
		                },
		                "ph": {
		                    "iOrder": -16,
		                    "inMatrix": true
		                },
		                "remark": {
		                    "sTitle": "Remarks",
		                    "iOrder": -19
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -21,
		                    "inMatrix": true
		                },
		                "result": {
		                    "iOrder": -20,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -9,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "sTitle": "Reference",
		                    "iOrder": -11
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "iOrder": -10,
		                    "bVisible": false
		                }
		            }
		        },
		        "SURFACE_CHEMISTRY_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -25
		                },
		                "method details": {
		                    "iOrder": -11,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "sTitle": "Sampling",
		                    "iOrder": -9,
		                    "bVisible": false
		                },
		                "functionalization": {
		                    "bVisible": false
		                },
		                "coating": {
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -8,
		                    "bVisible": false
		                }
		            },
		            "conditions": {
		                "type": {
		                    "sTitle": "",
		                    "iOrder": -23
		                },
		                "description": {
		                    "sTitle": "Type",
		                    "iOrder": -22,
		                    "inMatrix": true
		                },
		                "coating_description": {
		                    "sTitle": "Coating description",
		                    "iOrder": -21,
		                    "bVisible": false,
		                    "inMatrix": true
		                },
		                "remark": {
		                    "sTitle": "Remarks",
		                    "iOrder": -15,
		                    "bVisible": false
		                },
		                "element_or_group": {
		                    "sTitle": "Element / Func. group",
		                    "iOrder": -19,
		                    "bVisible": false,
		                    "inMatrix": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -20,
		                    "bVisible": false
		                },
		                "result": {
		                    "sTitle": "Fraction",
		                    "iOrder": -18,
		                    "inMatrix": true
		                },
		                "text": {
		                    "sTitle": "Element / Func. group",
		                    "iOrder": -19,
		                    "bVisible": true,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "iOrder": -11,
		                    "sTitle": "Reference"
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "bVisible": false
		                },
		                "criteria": {
		                    "sTitle": "Coating / Functionalisation",
		                    "bVisible": true,
		                    "iOrder": -24
		                }
		            }
		        },
		        "PC_GRANULOMETRY_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -25
		                },
		                "distribution_type": {
		                    "sTitle": "Distribution type",
		                    "iOrder": -23
		                },
		                "testmat_form": {
		                    "sTitle": "Test Material Form",
		                    "bVisible": false,
		                    "iOrder": -24
		                }
		            },
		            "conditions": {
		                "seq_num": {
		                    "sTitle": "Passage num.",
		                    "iOrder": -22,
		                    "bVisible": false
		                },
		                "medium": {
		                    "sTitle": "Medium",
		                    "iOrder": -14,
		                    "bVisible": true,
		                    "inMatrix": true
		                },
		                "remark": {
		                    "sTitle": "Remark",
		                    "iOrder": -15,
		                    "bVisible": false
		                },
		                "n": {
		                    "iOrder": -16
		                },
		                "phraseother_percentile": {
		                    "bVisible": false
		                },
		                "std_dev": {
		                    "bVisible": false
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -20,
		                    "bVisible": true,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Value",
		                    "iOrder": -18,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -9,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "sTitle": "Reference",
		                    "iOrder": -11
		                },
		                "uuid": {
		                    "bVisible": false
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "bVisible": false
		                },
		                "criteria": {
		                    "bVisible": false
		                }
		            }
		        },
		        "CRYSTALLITE_AND_GRAIN_SIZE_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -23,
		                    "sTitle": "Method type"
		                },
		                "method details": {
		                    "iOrder": -22,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "sTitle": "Sampling",
		                    "iOrder": -21,
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -20,
		                    "bVisible": false
		                },
		                "testmat_form": {
		                    "sTitle": "Test Material Form",
		                    "bVisible": true,
		                    "iOrder": -19
		                },
		                "material_isotropic": {
		                    "sTitle": "Isotropic material",
		                    "iOrder": -18
		                }
		            },
		            "conditions": {
		                "medium": {
		                    "sTitle": "Medium",
		                    "iOrder": -15,
		                    "inMatrix": true
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -17,
		                    "bVisible": true,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Result",
		                    "iOrder": -16,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "sTitle": "Reference",
		                    "iOrder": -11
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "bVisible": false
		                },
		                "criteria": {
		                    "bVisible": false
		                }
		            }
		        },
		        "DUSTINESS_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -23,
		                    "sTitle": "Method type"
		                },
		                "method details": {
		                    "iOrder": -22,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "sTitle": "Sampling",
		                    "iOrder": -21,
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -20,
		                    "bVisible": false
		                },
		                "testmat_form": {
		                    "sTitle": "Test Material Form",
		                    "bVisible": true,
		                    "iOrder": -19
		                }
		            },
		            "conditions": {},
		            "effects": {
		                "endpoint": {
		                    "iOrder": -17,
		                    "bVisible": false,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Dustiness index",
		                    "iOrder": -16,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "sTitle": "Reference",
		                    "iOrder": -11
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "bVisible": false
		                },
		                "criteria": {
		                    "bVisible": false
		                }
		            }
		        },
		        "POROSITY_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -23,
		                    "sTitle": "Method type"
		                },
		                "method details": {
		                    "iOrder": -22,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "sTitle": "Sampling",
		                    "iOrder": -21,
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -20,
		                    "bVisible": false
		                },
		                "testmat_form": {
		                    "sTitle": "Test Material Form",
		                    "bVisible": true,
		                    "iOrder": -19
		                }
		            },
		            "conditions": {},
		            "effects": {
		                "endpoint": {
		                    "iOrder": -17,
		                    "bVisible": true,
		                    "inMatrix": true
		                },
		                "result": {
		                    "iOrder": -16,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "iOrder": -11,
		                    "sTitle": "Reference"
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "bVisible": false
		                },
		                "criteria": {
		                    "bVisible": false
		                }
		            }
		        },
		        "SPECIFIC_SURFACE_AREA_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -23,
		                    "sTitle": "Method type"
		                },
		                "method details": {
		                    "iOrder": -22,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "sTitle": "Sampling",
		                    "iOrder": -21,
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -20,
		                    "bVisible": false
		                },
		                "testmat_form": {
		                    "sTitle": "Test Material Form",
		                    "bVisible": true,
		                    "iOrder": -19
		                }
		            },
		            "conditions": {
		                "remark": {
		                    "sTitle": "Remarks",
		                    "iOrder": -14
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -17,
		                    "bVisible": false,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Specific Surface Area",
		                    "iOrder": -16,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "iOrder": -11,
		                    "sTitle": "Reference"
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "bVisible": false
		                },
		                "criteria": {
		                    "bVisible": false
		                }
		            }
		        },
		        "POUR_DENSITY_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -23,
		                    "sTitle": "Method type"
		                },
		                "method details": {
		                    "iOrder": -22,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "sTitle": "Sampling",
		                    "iOrder": -21,
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -20,
		                    "bVisible": false
		                },
		                "testmat_form": {
		                    "sTitle": "Test Material Form",
		                    "bVisible": true,
		                    "iOrder": -19
		                }
		            },
		            "conditions": {},
		            "effects": {
		                "endpoint": {
		                    "iOrder": -17,
		                    "bVisible": false,
		                    "inMatrix": true
		                },
		                "result": {
		                    "sTitle": "Pour density",
		                    "iOrder": -16,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "iOrder": -11,
		                    "sTitle": "Reference"
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "iOrder": -14,
		                    "bVisible": true
		                },
		                "criteria": {
		                    "bVisible": false
		                }
		            }
		        },
		        "PHOTOCATALYTIC_ACTIVITY_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -23,
		                    "sTitle": "Method type"
		                },
		                "method details": {
		                    "iOrder": -22,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "sTitle": "Sampling",
		                    "iOrder": -21,
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -20,
		                    "bVisible": false
		                },
		                "testmat_form": {
		                    "sTitle": "Test Material Form",
		                    "bVisible": true,
		                    "iOrder": -19
		                }
		            },
		            "conditions": {},
		            "effects": {
		                "endpoint": {
		                    "iOrder": -17,
		                    "inMatrix": true
		                },
		                "result": {
		                    "iOrder": -16,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "sTitle": "Reference"
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "iOrder": -14,
		                    "bVisible": true
		                },
		                "criteria": {
		                    "iOrder": -11,
		                    "bVisible": false
		                }
		            }
		        },
		        "CATALYTIC_ACTIVITY_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -23,
		                    "sTitle": "Method type"
		                },
		                "method details": {
		                    "iOrder": -22,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "sTitle": "Sampling",
		                    "iOrder": -21,
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -20,
		                    "bVisible": false
		                },
		                "testmat_form": {
		                    "sTitle": "Test Material Form",
		                    "bVisible": true,
		                    "iOrder": -19
		                }
		            },
		            "conditions": {},
		            "effects": {
		                "endpoint": {
		                    "iOrder": -17,
		                    "inMatrix": true
		                },
		                "result": {
		                    "iOrder": -16,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -10,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true,
		                    "sTitle": "Reference",
		                    "iOrder": -11
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "iOrder": -14,
		                    "bVisible": true
		                },
		                "criteria": {
		                    "bVisible": false
		                }
		            }
		        },
		        "CRYSTALLINE_PHASE_SECTION": {
		            "parameters": {
		                "type of method": {
		                    "iOrder": -23,
		                    "sTitle": "Method type"
		                },
		                "method details": {
		                    "iOrder": -22,
		                    "bVisible": false
		                },
		                "sampling": {
		                    "sTitle": "Sampling",
		                    "iOrder": -21,
		                    "bVisible": false
		                },
		                "data_gathering_instruments": {
		                    "sTitle": "Instruments",
		                    "iOrder": -20,
		                    "bVisible": false
		                },
		                "testmat_form": {
		                    "sTitle": "Test Material Form",
		                    "bVisible": true,
		                    "iOrder": -19
		                }
		            },
		            "conditions": {
		                "crystal system": {
		                    "sTitle": "Crystal system",
		                    "iOrder": -18,
		                    "inMatrix": true
		                },
		                "common name": {
		                    "sTitle": "Common name",
		                    "iOrder": -17
		                },
		                "bravais lattice": {
		                    "sTitle": "Bravais lattice",
		                    "iOrder": -16
		                },
		                "point group": {
		                    "sTitle": "Point group",
		                    "iOrder": -15
		                },
		                "space group": {
		                    "sTitle": "Space group",
		                    "iOrder": -14
		                },
		                "crystallographic planes": {
		                    "sTitle": "Crystallographic planes",
		                    "iOrder": -13
		                }
		            },
		            "effects": {
		                "endpoint": {
		                    "iOrder": -12,
		                    "bVisible": false,
		                    "inMatrix": true
		                },
		                "result": {
		                    "bVisible": false,
		                    "iOrder": -11,
		                    "inMatrix": true
		                }
		            },
		            "protocol": {
		                "guideline": {
		                    "iOrder": -5,
		                    "inMatrix": true
		                },
		                "citation": {
		                    "bVisible": true
		                }
		            },
		            "interpretation": {
		                "result": {
		                    "sTitle": "Conclusions",
		                    "iOrder": -10,
		                    "bVisible": false
		                },
		                "criteria": {
		                    "bVisible": false
		                }
		            }
		        }
		     
}