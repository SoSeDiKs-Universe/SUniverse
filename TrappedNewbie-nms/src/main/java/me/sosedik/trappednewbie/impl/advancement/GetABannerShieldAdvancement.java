package me.sosedik.trappednewbie.impl.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancementBuilderImpl;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class GetABannerShieldAdvancement extends BaseAdvancement {

	public GetABannerShieldAdvancement(BaseAdvancementBuilderImpl advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		JsonObject json = JsonParser.parseString(BANNER_SHIELDS).getAsJsonObject();
		return RequiredAdvancementProgress.vanillaCriteria(List.of(new ArrayList<>(json.keySet())), json);
	}

	private static final String BANNER_SHIELDS = """
			{
			  "white": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "white"
			          }
			        }
			      ]
			    }
			  },
			  "orange": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "orange"
			          }
			        }
			      ]
			    }
			  },
			  "magenta": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "magenta"
			          }
			        }
			      ]
			    }
			  },
			  "light_blue": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "light_blue"
			          }
			        }
			      ]
			    }
			  },
			  "yellow": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "yellow"
			          }
			        }
			      ]
			    }
			  },
			  "lime": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "lime"
			          }
			        }
			      ]
			    }
			  },
			  "pink": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "pink"
			          }
			        }
			      ]
			    }
			  },
			  "gray": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "gray"
			          }
			        }
			      ]
			    }
			  },
			  "light_gray": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "light_gray"
			          }
			        }
			      ]
			    }
			  },
			  "cyan": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "cyan"
			          }
			        }
			      ]
			    }
			  },
			  "purple": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "purple"
			          }
			        }
			      ]
			    }
			  },
			  "blue": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "blue"
			          }
			        }
			      ]
			    }
			  },
			  "brown": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "brown"
			          }
			        }
			      ]
			    }
			  },
			  "green": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "green"
			          }
			        }
			      ]
			    }
			  },
			  "red": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "red"
			          }
			        }
			      ]
			    }
			  },
			  "black": {
			    "trigger": "minecraft:inventory_changed",
			    "conditions": {
			      "items": [
			        {
			          "items": [
			            "minecraft:shield"
			          ],
			          "components": {
			            "minecraft:base_color": "black"
			          }
			        }
			      ]
			    }
			  }
			}
			""";

}
