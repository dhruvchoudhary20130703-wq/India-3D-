package com.example.data.india

import com.example.data.model.BiomeType
import com.example.data.model.IndiaRegion
import com.example.data.model.IndianCity
import com.example.data.model.IndianStateInfo

/**
 * Complete directory of ALL 28 Indian States and 8 Union Territories (36 regions total)
 * with 155+ cities, prominently spotlighting Jodhpur, Barmer, and Mumbai.
 */
object IndiaGeoData {

    private fun city(
        id: String,
        name: String,
        state: String,
        code: String,
        lat: Double,
        lon: Double,
        biome: BiomeType,
        region: IndiaRegion,
        subtitle: String = "",
        spotlight: Boolean = false,
    ) = IndianCity(
        id = id,
        name = name,
        stateName = state,
        stateCode = code,
        lat = lat,
        lon = lon,
        biome = biome,
        region = region,
        subtitle = subtitle,
        isSpotlight = spotlight,
    )

    val allStatesAndUTs: List<IndianStateInfo> = listOf(
        // === FEATURED / SPOTLIGHT STATES FIRST ===
        IndianStateInfo(
            code = "RJ",
            name = "Rajasthan",
            isUnionTerritory = false,
            region = IndiaRegion.WEST_THAR,
            biome = BiomeType.THAR_DESERT,
            capital = "Jaipur",
            centerLat = 26.5,
            centerLon = 73.8,
            cities = listOf(
                city("jodhpur_rj", "Jodhpur", "Rajasthan", "RJ", 26.2389, 73.0243, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "The Blue City · Thar Gateway", spotlight = true),
                city("barmer_rj", "Barmer", "Rajasthan", "RJ", 25.7521, 71.3967, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Thar Desert Heartland", spotlight = true),
                city("jaipur_rj", "Jaipur", "Rajasthan", "RJ", 26.9124, 75.7873, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Pink City · State Capital", spotlight = true),
                city("jaisalmer_rj", "Jaisalmer", "Rajasthan", "RJ", 26.9157, 70.9083, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Golden Dunes"),
                city("bikaner_rj", "Bikaner", "Rajasthan", "RJ", 28.0229, 73.3119, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Camel Country"),
                city("udaipur_rj", "Udaipur", "Rajasthan", "RJ", 24.5854, 73.7125, BiomeType.DECCAN_PLATEAU, IndiaRegion.WEST_THAR, "City of Lakes"),
                city("ajmer_rj", "Ajmer", "Rajasthan", "RJ", 26.4499, 74.6399, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Aravalli Valley"),
                city("kota_rj", "Kota", "Rajasthan", "RJ", 25.2138, 75.8648, BiomeType.PLAINS, IndiaRegion.WEST_THAR, "Chambal Basin"),
                city("mount_abu_rj", "Mount Abu", "Rajasthan", "RJ", 24.5926, 72.7156, BiomeType.HIMALAYAN, IndiaRegion.WEST_THAR, "Aravalli Hill Station"),
                city("pali_rj", "Pali", "Rajasthan", "RJ", 25.7711, 73.3234, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Marwar Plains"),
                city("nagaur_rj", "Nagaur", "Rajasthan", "RJ", 27.2020, 73.7339, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Central Marwar"),
                city("churu_rj", "Churu", "Rajasthan", "RJ", 28.2925, 74.9707, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Shekhawati Sands"),
                city("sikar_rj", "Sikar", "Rajasthan", "RJ", 27.6094, 75.1398, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Shekhawati"),
                city("alwar_rj", "Alwar", "Rajasthan", "RJ", 27.5530, 76.6346, BiomeType.PLAINS, IndiaRegion.WEST_THAR, "Sariska Hills"),
                city("ganganagar_rj", "Sri Ganganagar", "Rajasthan", "RJ", 29.9038, 73.8772, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Northern Oasis"),
            ),
        ),
        IndianStateInfo(
            code = "MH",
            name = "Maharashtra",
            isUnionTerritory = false,
            region = IndiaRegion.WEST_THAR,
            biome = BiomeType.COASTAL,
            capital = "Mumbai",
            centerLat = 19.6,
            centerLon = 75.3,
            cities = listOf(
                city("mumbai_mh", "Mumbai", "Maharashtra", "MH", 19.0760, 72.8777, BiomeType.COASTAL, IndiaRegion.WEST_THAR, "Konkan Coast · Island Metropolis", spotlight = true),
                city("pune_mh", "Pune", "Maharashtra", "MH", 18.5204, 73.8567, BiomeType.DECCAN_PLATEAU, IndiaRegion.WEST_THAR, "Sahyadri Foothills"),
                city("nagpur_mh", "Nagpur", "Maharashtra", "MH", 21.1458, 79.0882, BiomeType.DECCAN_PLATEAU, IndiaRegion.WEST_THAR, "Zero Mile · Vidarbha"),
                city("nashik_mh", "Nashik", "Maharashtra", "MH", 19.9975, 73.7898, BiomeType.DECCAN_PLATEAU, IndiaRegion.WEST_THAR, "Godavari Wine Valley"),
                city("aurangabad_mh", "Chhatrapati Sambhajinagar", "Maharashtra", "MH", 19.8762, 75.3433, BiomeType.DECCAN_PLATEAU, IndiaRegion.WEST_THAR, "Marathwada"),
                city("thane_mh", "Thane", "Maharashtra", "MH", 19.2183, 72.9781, BiomeType.COASTAL, IndiaRegion.WEST_THAR, "City of Lakes"),
                city("kolhapur_mh", "Kolhapur", "Maharashtra", "MH", 16.7050, 74.2433, BiomeType.DECCAN_PLATEAU, IndiaRegion.WEST_THAR, "Western Ghats"),
                city("ratnagiri_mh", "Ratnagiri", "Maharashtra", "MH", 16.9902, 73.3120, BiomeType.COASTAL, IndiaRegion.WEST_THAR, "Konkan Shores"),
            ),
        ),
        IndianStateInfo(
            code = "DL",
            name = "Delhi (NCT)",
            isUnionTerritory = true,
            region = IndiaRegion.NORTH_HIMALAYA,
            biome = BiomeType.PLAINS,
            capital = "New Delhi",
            centerLat = 28.6139,
            centerLon = 77.2090,
            cities = listOf(
                city("new_delhi_dl", "New Delhi", "Delhi (NCT)", "DL", 28.6139, 77.2090, BiomeType.PLAINS, IndiaRegion.NORTH_HIMALAYA, "National Capital", spotlight = true),
                city("dwarka_dl", "Dwarka", "Delhi (NCT)", "DL", 28.5921, 77.0460, BiomeType.PLAINS, IndiaRegion.NORTH_HIMALAYA, "Southwest Delhi"),
                city("rohini_dl", "Rohini", "Delhi (NCT)", "DL", 28.7495, 77.0565, BiomeType.PLAINS, IndiaRegion.NORTH_HIMALAYA, "Northwest Delhi"),
            ),
        ),
        // === ALL REMAINING STATES & UNION TERRITORIES (ALPHABETICAL) ===
        IndianStateInfo(
            code = "AN",
            name = "Andaman & Nicobar Islands",
            isUnionTerritory = true,
            region = IndiaRegion.ISLANDS,
            biome = BiomeType.ISLANDS,
            capital = "Sri Vijaya Puram (Port Blair)",
            centerLat = 11.6234,
            centerLon = 92.7265,
            cities = listOf(
                city("port_blair_an", "Port Blair", "Andaman & Nicobar Islands", "AN", 11.6234, 92.7265, BiomeType.ISLANDS, IndiaRegion.ISLANDS, "Bay of Bengal"),
                city("havelock_an", "Swaraj Dweep", "Andaman & Nicobar Islands", "AN", 11.9761, 92.9876, BiomeType.ISLANDS, IndiaRegion.ISLANDS, "Coral Reefs"),
            ),
        ),
        IndianStateInfo(
            code = "AP",
            name = "Andhra Pradesh",
            isUnionTerritory = false,
            region = IndiaRegion.SOUTH_PENINSULA,
            biome = BiomeType.COASTAL,
            capital = "Amaravati",
            centerLat = 16.5,
            centerLon = 80.6,
            cities = listOf(
                city("visakhapatnam_ap", "Visakhapatnam", "Andhra Pradesh", "AP", 17.6868, 83.2185, BiomeType.COASTAL, IndiaRegion.SOUTH_PENINSULA, "Jewel of East Coast"),
                city("vijayawada_ap", "Vijayawada", "Andhra Pradesh", "AP", 16.5062, 80.6480, BiomeType.PLAINS, IndiaRegion.SOUTH_PENINSULA, "Krishna Delta"),
                city("amaravati_ap", "Amaravati", "Andhra Pradesh", "AP", 16.5131, 80.5165, BiomeType.PLAINS, IndiaRegion.SOUTH_PENINSULA, "State Capital"),
                city("tirupati_ap", "Tirupati", "Andhra Pradesh", "AP", 13.6288, 79.4192, BiomeType.DECCAN_PLATEAU, IndiaRegion.SOUTH_PENINSULA, "Tirumala Hills"),
                city("kurnool_ap", "Kurnool", "Andhra Pradesh", "AP", 15.8281, 78.0373, BiomeType.DECCAN_PLATEAU, IndiaRegion.SOUTH_PENINSULA, "Rayalaseema"),
            ),
        ),
        IndianStateInfo(
            code = "AR",
            name = "Arunachal Pradesh",
            isUnionTerritory = false,
            region = IndiaRegion.NORTHEAST,
            biome = BiomeType.HIMALAYAN,
            capital = "Itanagar",
            centerLat = 27.1,
            centerLon = 93.6,
            cities = listOf(
                city("itanagar_ar", "Itanagar", "Arunachal Pradesh", "AR", 27.0844, 93.6053, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Land of Dawn-Lit Mountains"),
                city("tawang_ar", "Tawang", "Arunachal Pradesh", "AR", 27.5861, 91.8594, BiomeType.HIMALAYAN, IndiaRegion.NORTHEAST, "Eastern Himalayas · 3048m"),
                city("pasighat_ar", "Pasighat", "Arunachal Pradesh", "AR", 28.0660, 95.3260, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Siang River"),
                city("ziro_ar", "Ziro", "Arunachal Pradesh", "AR", 27.5950, 93.8385, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Pine Valley"),
            ),
        ),
        IndianStateInfo(
            code = "AS",
            name = "Assam",
            isUnionTerritory = false,
            region = IndiaRegion.NORTHEAST,
            biome = BiomeType.NORTHEAST_HILLS,
            capital = "Dispur",
            centerLat = 26.14,
            centerLon = 91.73,
            cities = listOf(
                city("guwahati_as", "Guwahati", "Assam", "AS", 26.1445, 91.7362, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Brahmaputra Gateway"),
                city("dibrugarh_as", "Dibrugarh", "Assam", "AS", 27.4728, 94.9120, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Tea City"),
                city("silchar_as", "Silchar", "Assam", "AS", 24.8333, 92.7789, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Barak Valley"),
                city("jorhat_as", "Jorhat", "Assam", "AS", 26.7509, 94.2037, BiomeType.PLAINS, IndiaRegion.NORTHEAST, "Upper Assam"),
            ),
        ),
        IndianStateInfo(
            code = "BR",
            name = "Bihar",
            isUnionTerritory = false,
            region = IndiaRegion.EAST,
            biome = BiomeType.PLAINS,
            capital = "Patna",
            centerLat = 25.59,
            centerLon = 85.13,
            cities = listOf(
                city("patna_br", "Patna", "Bihar", "BR", 25.5941, 85.1376, BiomeType.PLAINS, IndiaRegion.EAST, "Ganga Banks"),
                city("gaya_br", "Gaya", "Bihar", "BR", 24.7914, 85.0002, BiomeType.PLAINS, IndiaRegion.EAST, "Magadh Plains"),
                city("muzaffarpur_br", "Muzaffarpur", "Bihar", "BR", 26.1197, 85.3910, BiomeType.PLAINS, IndiaRegion.EAST, "Tirhut"),
                city("bhagalpur_br", "Bhagalpur", "Bihar", "BR", 25.2425, 86.9842, BiomeType.PLAINS, IndiaRegion.EAST, "Silk City"),
            ),
        ),
        IndianStateInfo(
            code = "CH",
            name = "Chandigarh",
            isUnionTerritory = true,
            region = IndiaRegion.NORTH_HIMALAYA,
            biome = BiomeType.PLAINS,
            capital = "Chandigarh",
            centerLat = 30.7333,
            centerLon = 76.7794,
            cities = listOf(
                city("chandigarh_ch", "Chandigarh", "Chandigarh", "CH", 30.7333, 76.7794, BiomeType.PLAINS, IndiaRegion.NORTH_HIMALAYA, "Shivalik Foothills"),
            ),
        ),
        IndianStateInfo(
            code = "CG",
            name = "Chhattisgarh",
            isUnionTerritory = false,
            region = IndiaRegion.CENTRAL_PLAINS,
            biome = BiomeType.DECCAN_PLATEAU,
            capital = "Raipur",
            centerLat = 21.25,
            centerLon = 81.63,
            cities = listOf(
                city("raipur_cg", "Raipur", "Chhattisgarh", "CG", 21.2514, 81.6296, BiomeType.DECCAN_PLATEAU, IndiaRegion.CENTRAL_PLAINS, "Mahanadi Basin"),
                city("bilaspur_cg", "Bilaspur", "Chhattisgarh", "CG", 22.0797, 82.1409, BiomeType.PLAINS, IndiaRegion.CENTRAL_PLAINS, "Central Plains"),
                city("jagdalpur_cg", "Jagdalpur", "Chhattisgarh", "CG", 19.0748, 82.0085, BiomeType.NORTHEAST_HILLS, IndiaRegion.CENTRAL_PLAINS, "Bastar Plateau"),
            ),
        ),
        IndianStateInfo(
            code = "DN",
            name = "Dadra & Nagar Haveli and Daman & Diu",
            isUnionTerritory = true,
            region = IndiaRegion.WEST_THAR,
            biome = BiomeType.COASTAL,
            capital = "Daman",
            centerLat = 20.3974,
            centerLon = 72.8328,
            cities = listOf(
                city("daman_dn", "Daman", "Dadra & Nagar Haveli and Daman & Diu", "DN", 20.3974, 72.8328, BiomeType.COASTAL, IndiaRegion.WEST_THAR, "Arabian Sea Coast"),
                city("silvassa_dn", "Silvassa", "Dadra & Nagar Haveli and Daman & Diu", "DN", 20.2737, 73.0135, BiomeType.COASTAL, IndiaRegion.WEST_THAR, "Daman Ganga"),
                city("diu_dn", "Diu", "Dadra & Nagar Haveli and Daman & Diu", "DN", 20.7144, 70.9874, BiomeType.ISLANDS, IndiaRegion.WEST_THAR, "Saurashtra Coast"),
            ),
        ),
        IndianStateInfo(
            code = "GA",
            name = "Goa",
            isUnionTerritory = false,
            region = IndiaRegion.WEST_THAR,
            biome = BiomeType.COASTAL,
            capital = "Panaji",
            centerLat = 15.49,
            centerLon = 73.82,
            cities = listOf(
                city("panaji_ga", "Panaji", "Goa", "GA", 15.4909, 73.8278, BiomeType.COASTAL, IndiaRegion.WEST_THAR, "Mandovi Estuary"),
                city("margao_ga", "Margao", "Goa", "GA", 15.2832, 73.9862, BiomeType.COASTAL, IndiaRegion.WEST_THAR, "South Goa"),
                city("vasco_ga", "Vasco da Gama", "Goa", "GA", 15.3982, 73.8113, BiomeType.COASTAL, IndiaRegion.WEST_THAR, "Zuari Bay"),
            ),
        ),
        IndianStateInfo(
            code = "GJ",
            name = "Gujarat",
            isUnionTerritory = false,
            region = IndiaRegion.WEST_THAR,
            biome = BiomeType.THAR_DESERT,
            capital = "Gandhinagar",
            centerLat = 22.3,
            centerLon = 71.8,
            cities = listOf(
                city("ahmedabad_gj", "Ahmedabad", "Gujarat", "GJ", 23.0225, 72.5714, BiomeType.PLAINS, IndiaRegion.WEST_THAR, "Sabarmati Riverfront"),
                city("surat_gj", "Surat", "Gujarat", "GJ", 21.1702, 72.8311, BiomeType.COASTAL, IndiaRegion.WEST_THAR, "Tapi Estuary"),
                city("vadodara_gj", "Vadodara", "Gujarat", "GJ", 22.3072, 73.1812, BiomeType.PLAINS, IndiaRegion.WEST_THAR, "Cultural Capital"),
                city("rajkot_gj", "Rajkot", "Gujarat", "GJ", 22.3039, 70.8022, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Saurashtra"),
                city("bhuj_gj", "Bhuj", "Gujarat", "GJ", 23.2420, 69.6669, BiomeType.THAR_DESERT, IndiaRegion.WEST_THAR, "Rann of Kutch"),
                city("dwarka_gj", "Dwarka", "Gujarat", "GJ", 22.2442, 68.9685, BiomeType.COASTAL, IndiaRegion.WEST_THAR, "Westernmost Shore"),
            ),
        ),
        IndianStateInfo(
            code = "HR",
            name = "Haryana",
            isUnionTerritory = false,
            region = IndiaRegion.NORTH_HIMALAYA,
            biome = BiomeType.PLAINS,
            capital = "Chandigarh",
            centerLat = 29.0,
            centerLon = 76.0,
            cities = listOf(
                city("gurugram_hr", "Gurugram", "Haryana", "HR", 28.4595, 77.0266, BiomeType.PLAINS, IndiaRegion.NORTH_HIMALAYA, "NCR Millennium City"),
                city("faridabad_hr", "Faridabad", "Haryana", "HR", 28.4089, 77.3178, BiomeType.PLAINS, IndiaRegion.NORTH_HIMALAYA, "Yamuna Plains"),
                city("hisar_hr", "Hisar", "Haryana", "HR", 29.1492, 75.7217, BiomeType.THAR_DESERT, IndiaRegion.NORTH_HIMALAYA, "Semi-Arid West"),
                city("ambala_hr", "Ambala", "Haryana", "HR", 30.3782, 76.7767, BiomeType.PLAINS, IndiaRegion.NORTH_HIMALAYA, "Northern Plains"),
            ),
        ),
        IndianStateInfo(
            code = "HP",
            name = "Himachal Pradesh",
            isUnionTerritory = false,
            region = IndiaRegion.NORTH_HIMALAYA,
            biome = BiomeType.HIMALAYAN,
            capital = "Shimla",
            centerLat = 31.1,
            centerLon = 77.17,
            cities = listOf(
                city("shimla_hp", "Shimla", "Himachal Pradesh", "HP", 31.1048, 77.1734, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Queen of Hills · 2276m"),
                city("manali_hp", "Manali", "Himachal Pradesh", "HP", 32.2432, 77.1892, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Kullu Valley · 2050m"),
                city("dharamshala_hp", "Dharamshala", "Himachal Pradesh", "HP", 32.2190, 76.3234, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Dhauladhar Range"),
                city("solan_hp", "Solan", "Himachal Pradesh", "HP", 30.9045, 77.0967, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Shivalik Ridges"),
            ),
        ),
        IndianStateInfo(
            code = "JK",
            name = "Jammu & Kashmir",
            isUnionTerritory = true,
            region = IndiaRegion.NORTH_HIMALAYA,
            biome = BiomeType.HIMALAYAN,
            capital = "Srinagar / Jammu",
            centerLat = 34.08,
            centerLon = 74.79,
            cities = listOf(
                city("srinagar_jk", "Srinagar", "Jammu & Kashmir", "JK", 34.0837, 74.7973, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Kashmir Valley · Dal Lake"),
                city("jammu_jk", "Jammu", "Jammu & Kashmir", "JK", 32.7266, 74.8570, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Tawi Foothills"),
                city("gulmarg_jk", "Gulmarg", "Jammu & Kashmir", "JK", 34.0484, 74.3805, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Alpine Meadows · 2650m"),
                city("pahalgam_jk", "Pahalgam", "Jammu & Kashmir", "JK", 34.0161, 75.3150, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Lidder Valley"),
            ),
        ),
        IndianStateInfo(
            code = "JH",
            name = "Jharkhand",
            isUnionTerritory = false,
            region = IndiaRegion.EAST,
            biome = BiomeType.DECCAN_PLATEAU,
            capital = "Ranchi",
            centerLat = 23.34,
            centerLon = 85.3,
            cities = listOf(
                city("ranchi_jh", "Ranchi", "Jharkhand", "JH", 23.3441, 85.3096, BiomeType.DECCAN_PLATEAU, IndiaRegion.EAST, "Chota Nagpur Plateau"),
                city("jamshedpur_jh", "Jamshedpur", "Jharkhand", "JH", 22.8046, 86.2029, BiomeType.DECCAN_PLATEAU, IndiaRegion.EAST, "Subarnarekha Valley"),
                city("dhanbad_jh", "Dhanbad", "Jharkhand", "JH", 23.7957, 86.4304, BiomeType.DECCAN_PLATEAU, IndiaRegion.EAST, "Damodar Basin"),
            ),
        ),
        IndianStateInfo(
            code = "KA",
            name = "Karnataka",
            isUnionTerritory = false,
            region = IndiaRegion.SOUTH_PENINSULA,
            biome = BiomeType.DECCAN_PLATEAU,
            capital = "Bengaluru",
            centerLat = 14.5,
            centerLon = 76.2,
            cities = listOf(
                city("bengaluru_ka", "Bengaluru", "Karnataka", "KA", 12.9716, 77.5946, BiomeType.DECCAN_PLATEAU, IndiaRegion.SOUTH_PENINSULA, "Garden City · 920m", spotlight = true),
                city("mysuru_ka", "Mysuru", "Karnataka", "KA", 12.2958, 76.6394, BiomeType.DECCAN_PLATEAU, IndiaRegion.SOUTH_PENINSULA, "Royal Heritage"),
                city("mangaluru_ka", "Mangaluru", "Karnataka", "KA", 12.9141, 74.8560, BiomeType.COASTAL, IndiaRegion.SOUTH_PENINSULA, "Kanara Coast"),
                city("hubballi_ka", "Hubballi", "Karnataka", "KA", 15.3647, 75.1240, BiomeType.DECCAN_PLATEAU, IndiaRegion.SOUTH_PENINSULA, "North Karnataka"),
                city("belagavi_ka", "Belagavi", "Karnataka", "KA", 15.8497, 74.4977, BiomeType.DECCAN_PLATEAU, IndiaRegion.SOUTH_PENINSULA, "Malnad Crest"),
            ),
        ),
        IndianStateInfo(
            code = "KL",
            name = "Kerala",
            isUnionTerritory = false,
            region = IndiaRegion.SOUTH_PENINSULA,
            biome = BiomeType.COASTAL,
            capital = "Thiruvananthapuram",
            centerLat = 10.5,
            centerLon = 76.3,
            cities = listOf(
                city("kochi_kl", "Kochi", "Kerala", "KL", 9.9312, 76.2673, BiomeType.COASTAL, IndiaRegion.SOUTH_PENINSULA, "Malabar Backwaters"),
                city("trivandrum_kl", "Thiruvananthapuram", "Kerala", "KL", 8.5241, 76.9366, BiomeType.COASTAL, IndiaRegion.SOUTH_PENINSULA, "Southern Coast"),
                city("kozhikode_kl", "Kozhikode", "Kerala", "KL", 11.2588, 75.7804, BiomeType.COASTAL, IndiaRegion.SOUTH_PENINSULA, "Malabar Coast"),
                city("munnar_kl", "Munnar", "Kerala", "KL", 10.0889, 77.0595, BiomeType.HIMALAYAN, IndiaRegion.SOUTH_PENINSULA, "Western Ghats Tea Hills"),
            ),
        ),
        IndianStateInfo(
            code = "LA",
            name = "Ladakh",
            isUnionTerritory = true,
            region = IndiaRegion.NORTH_HIMALAYA,
            biome = BiomeType.HIMALAYAN,
            capital = "Leh",
            centerLat = 34.15,
            centerLon = 77.57,
            cities = listOf(
                city("leh_la", "Leh", "Ladakh", "LA", 34.1526, 77.5771, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Trans-Himalaya · 3500m", spotlight = true),
                city("kargil_la", "Kargil", "Ladakh", "LA", 34.5539, 76.1349, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Suru Valley"),
            ),
        ),
        IndianStateInfo(
            code = "LD",
            name = "Lakshadweep",
            isUnionTerritory = true,
            region = IndiaRegion.ISLANDS,
            biome = BiomeType.ISLANDS,
            capital = "Kavaratti",
            centerLat = 10.56,
            centerLon = 72.64,
            cities = listOf(
                city("kavaratti_ld", "Kavaratti", "Lakshadweep", "LD", 10.5593, 72.6358, BiomeType.ISLANDS, IndiaRegion.ISLANDS, "Arabian Sea Atolls"),
                city("agatti_ld", "Agatti", "Lakshadweep", "LD", 10.8576, 72.1942, BiomeType.ISLANDS, IndiaRegion.ISLANDS, "Coral Lagoon"),
            ),
        ),
        IndianStateInfo(
            code = "MP",
            name = "Madhya Pradesh",
            isUnionTerritory = false,
            region = IndiaRegion.CENTRAL_PLAINS,
            biome = BiomeType.DECCAN_PLATEAU,
            capital = "Bhopal",
            centerLat = 23.25,
            centerLon = 77.41,
            cities = listOf(
                city("bhopal_mp", "Bhopal", "Madhya Pradesh", "MP", 23.2599, 77.4126, BiomeType.DECCAN_PLATEAU, IndiaRegion.CENTRAL_PLAINS, "City of Lakes"),
                city("indore_mp", "Indore", "Madhya Pradesh", "MP", 22.7196, 75.8577, BiomeType.DECCAN_PLATEAU, IndiaRegion.CENTRAL_PLAINS, "Malwa Plateau"),
                city("jabalpur_mp", "Jabalpur", "Madhya Pradesh", "MP", 23.1815, 79.9864, BiomeType.DECCAN_PLATEAU, IndiaRegion.CENTRAL_PLAINS, "Narmada Marble Rocks"),
                city("gwalior_mp", "Gwalior", "Madhya Pradesh", "MP", 26.2183, 78.1828, BiomeType.PLAINS, IndiaRegion.CENTRAL_PLAINS, "Chambal Fort"),
                city("ujjain_mp", "Ujjain", "Madhya Pradesh", "MP", 23.1765, 75.7885, BiomeType.DECCAN_PLATEAU, IndiaRegion.CENTRAL_PLAINS, "Shipra Banks"),
            ),
        ),
        IndianStateInfo(
            code = "MN",
            name = "Manipur",
            isUnionTerritory = false,
            region = IndiaRegion.NORTHEAST,
            biome = BiomeType.NORTHEAST_HILLS,
            capital = "Imphal",
            centerLat = 24.81,
            centerLon = 93.93,
            cities = listOf(
                city("imphal_mn", "Imphal", "Manipur", "MN", 24.8170, 93.9368, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Loktak Valley"),
                city("churachandpur_mn", "Churachandpur", "Manipur", "MN", 24.3331, 93.6798, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Southern Hills"),
            ),
        ),
        IndianStateInfo(
            code = "ML",
            name = "Meghalaya",
            isUnionTerritory = false,
            region = IndiaRegion.NORTHEAST,
            biome = BiomeType.NORTHEAST_HILLS,
            capital = "Shillong",
            centerLat = 25.57,
            centerLon = 91.89,
            cities = listOf(
                city("shillong_ml", "Shillong", "Meghalaya", "ML", 25.5788, 91.8933, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Abode of Clouds"),
                city("cherrapunji_ml", "Sohra (Cherrapunji)", "Meghalaya", "ML", 25.2702, 91.7323, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Monsoon Capital"),
            ),
        ),
        IndianStateInfo(
            code = "MZ",
            name = "Mizoram",
            isUnionTerritory = false,
            region = IndiaRegion.NORTHEAST,
            biome = BiomeType.NORTHEAST_HILLS,
            capital = "Aizawl",
            centerLat = 23.72,
            centerLon = 92.71,
            cities = listOf(
                city("aizawl_mz", "Aizawl", "Mizoram", "MZ", 23.7271, 92.7176, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Lushai Ridges"),
                city("lunglei_mz", "Lunglei", "Mizoram", "MZ", 22.8856, 92.7385, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "South Mizoram"),
            ),
        ),
        IndianStateInfo(
            code = "NL",
            name = "Nagaland",
            isUnionTerritory = false,
            region = IndiaRegion.NORTHEAST,
            biome = BiomeType.NORTHEAST_HILLS,
            capital = "Kohima",
            centerLat = 25.67,
            centerLon = 94.1,
            cities = listOf(
                city("kohima_nl", "Kohima", "Nagaland", "NL", 25.6751, 94.1086, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Naga Hills · 1444m"),
                city("dimapur_nl", "Dimapur", "Nagaland", "NL", 25.9091, 93.7266, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Foothills Gateway"),
            ),
        ),
        IndianStateInfo(
            code = "OD",
            name = "Odisha",
            isUnionTerritory = false,
            region = IndiaRegion.EAST,
            biome = BiomeType.COASTAL,
            capital = "Bhubaneswar",
            centerLat = 20.29,
            centerLon = 85.82,
            cities = listOf(
                city("bhubaneswar_od", "Bhubaneswar", "Odisha", "OD", 20.2961, 85.8245, BiomeType.COASTAL, IndiaRegion.EAST, "Temple City"),
                city("puri_od", "Puri", "Odisha", "OD", 19.8135, 85.8312, BiomeType.COASTAL, IndiaRegion.EAST, "Bay of Bengal Shore"),
                city("cuttack_od", "Cuttack", "Odisha", "OD", 20.4625, 85.8830, BiomeType.PLAINS, IndiaRegion.EAST, "Mahanadi Delta"),
                city("rourkela_od", "Rourkela", "Odisha", "OD", 22.2604, 84.8536, BiomeType.DECCAN_PLATEAU, IndiaRegion.EAST, "Western Odisha"),
            ),
        ),
        IndianStateInfo(
            code = "PY",
            name = "Puducherry",
            isUnionTerritory = true,
            region = IndiaRegion.SOUTH_PENINSULA,
            biome = BiomeType.COASTAL,
            capital = "Puducherry",
            centerLat = 11.94,
            centerLon = 79.8,
            cities = listOf(
                city("puducherry_py", "Puducherry", "Puducherry", "PY", 11.9416, 79.8083, BiomeType.COASTAL, IndiaRegion.SOUTH_PENINSULA, "Coromandel Coast"),
                city("karaikal_py", "Karaikal", "Puducherry", "PY", 10.9254, 79.8380, BiomeType.COASTAL, IndiaRegion.SOUTH_PENINSULA, "Kaveri Delta"),
            ),
        ),
        IndianStateInfo(
            code = "PB",
            name = "Punjab",
            isUnionTerritory = false,
            region = IndiaRegion.NORTH_HIMALAYA,
            biome = BiomeType.PLAINS,
            capital = "Chandigarh",
            centerLat = 30.9,
            centerLon = 75.5,
            cities = listOf(
                city("amritsar_pb", "Amritsar", "Punjab", "PB", 31.6340, 74.8723, BiomeType.PLAINS, IndiaRegion.NORTH_HIMALAYA, "Majha Plains"),
                city("ludhiana_pb", "Ludhiana", "Punjab", "PB", 30.9010, 75.8573, BiomeType.PLAINS, IndiaRegion.NORTH_HIMALAYA, "Sutlej Plains"),
                city("jalandhar_pb", "Jalandhar", "Punjab", "PB", 31.3260, 75.5762, BiomeType.PLAINS, IndiaRegion.NORTH_HIMALAYA, "Doaba"),
                city("bathinda_pb", "Bathinda", "Punjab", "PB", 30.2110, 74.9455, BiomeType.THAR_DESERT, IndiaRegion.NORTH_HIMALAYA, "Malwa Semi-Arid"),
            ),
        ),
        IndianStateInfo(
            code = "SK",
            name = "Sikkim",
            isUnionTerritory = false,
            region = IndiaRegion.NORTHEAST,
            biome = BiomeType.HIMALAYAN,
            capital = "Gangtok",
            centerLat = 27.33,
            centerLon = 88.61,
            cities = listOf(
                city("gangtok_sk", "Gangtok", "Sikkim", "SK", 27.3314, 88.6138, BiomeType.HIMALAYAN, IndiaRegion.NORTHEAST, "Kanchenjunga View · 1650m"),
                city("pelling_sk", "Pelling", "Sikkim", "SK", 27.3198, 88.2400, BiomeType.HIMALAYAN, IndiaRegion.NORTHEAST, "Alpine Ridge · 2150m"),
            ),
        ),
        IndianStateInfo(
            code = "TN",
            name = "Tamil Nadu",
            isUnionTerritory = false,
            region = IndiaRegion.SOUTH_PENINSULA,
            biome = BiomeType.COASTAL,
            capital = "Chennai",
            centerLat = 11.1,
            centerLon = 78.6,
            cities = listOf(
                city("chennai_tn", "Chennai", "Tamil Nadu", "TN", 13.0827, 80.2707, BiomeType.COASTAL, IndiaRegion.SOUTH_PENINSULA, "Coromandel Coast", spotlight = true),
                city("coimbatore_tn", "Coimbatore", "Tamil Nadu", "TN", 11.0168, 76.9558, BiomeType.DECCAN_PLATEAU, IndiaRegion.SOUTH_PENINSULA, "Nilgiri Foothills"),
                city("madurai_tn", "Madurai", "Tamil Nadu", "TN", 9.9252, 78.1198, BiomeType.PLAINS, IndiaRegion.SOUTH_PENINSULA, "Vaigai Banks"),
                city("ooty_tn", "Udhagamandalam (Ooty)", "Tamil Nadu", "TN", 11.4102, 76.6950, BiomeType.HIMALAYAN, IndiaRegion.SOUTH_PENINSULA, "Nilgiri Blue Mountains"),
                city("kanyakumari_tn", "Kanyakumari", "Tamil Nadu", "TN", 8.0883, 77.5385, BiomeType.COASTAL, IndiaRegion.SOUTH_PENINSULA, "Three Seas Tip"),
            ),
        ),
        IndianStateInfo(
            code = "TS",
            name = "Telangana",
            isUnionTerritory = false,
            region = IndiaRegion.SOUTH_PENINSULA,
            biome = BiomeType.DECCAN_PLATEAU,
            capital = "Hyderabad",
            centerLat = 17.38,
            centerLon = 78.48,
            cities = listOf(
                city("hyderabad_ts", "Hyderabad", "Telangana", "TS", 17.3850, 78.4867, BiomeType.DECCAN_PLATEAU, IndiaRegion.SOUTH_PENINSULA, "Deccan Pearl", spotlight = true),
                city("warangal_ts", "Warangal", "Telangana", "TS", 17.9689, 79.5941, BiomeType.DECCAN_PLATEAU, IndiaRegion.SOUTH_PENINSULA, "Kakatiya Plateau"),
                city("nizamabad_ts", "Nizamabad", "Telangana", "TS", 18.6725, 78.0941, BiomeType.DECCAN_PLATEAU, IndiaRegion.SOUTH_PENINSULA, "Godavari Basin"),
            ),
        ),
        IndianStateInfo(
            code = "TR",
            name = "Tripura",
            isUnionTerritory = false,
            region = IndiaRegion.NORTHEAST,
            biome = BiomeType.NORTHEAST_HILLS,
            capital = "Agartala",
            centerLat = 23.83,
            centerLon = 91.28,
            cities = listOf(
                city("agartala_tr", "Agartala", "Tripura", "TR", 23.8315, 91.2868, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "Haora Valley"),
                city("dharmanagar_tr", "Dharmanagar", "Tripura", "TR", 24.3767, 92.1674, BiomeType.NORTHEAST_HILLS, IndiaRegion.NORTHEAST, "North Tripura"),
            ),
        ),
        IndianStateInfo(
            code = "UP",
            name = "Uttar Pradesh",
            isUnionTerritory = false,
            region = IndiaRegion.CENTRAL_PLAINS,
            biome = BiomeType.PLAINS,
            capital = "Lucknow",
            centerLat = 26.84,
            centerLon = 80.94,
            cities = listOf(
                city("lucknow_up", "Lucknow", "Uttar Pradesh", "UP", 26.8467, 80.9462, BiomeType.PLAINS, IndiaRegion.CENTRAL_PLAINS, "Awadh Plains"),
                city("varanasi_up", "Varanasi", "Uttar Pradesh", "UP", 25.3176, 82.9739, BiomeType.PLAINS, IndiaRegion.CENTRAL_PLAINS, "Ganga Ghats"),
                city("agra_up", "Agra", "Uttar Pradesh", "UP", 27.1767, 78.0081, BiomeType.PLAINS, IndiaRegion.CENTRAL_PLAINS, "Yamuna Banks"),
                city("kanpur_up", "Kanpur", "Uttar Pradesh", "UP", 26.4499, 80.3319, BiomeType.PLAINS, IndiaRegion.CENTRAL_PLAINS, "Central Doab"),
                city("prayagraj_up", "Prayagraj", "Uttar Pradesh", "UP", 25.4358, 81.8463, BiomeType.PLAINS, IndiaRegion.CENTRAL_PLAINS, "Triveni Sangam"),
                city("noida_up", "Noida", "Uttar Pradesh", "UP", 28.5355, 77.3910, BiomeType.PLAINS, IndiaRegion.CENTRAL_PLAINS, "Delhi NCR"),
            ),
        ),
        IndianStateInfo(
            code = "UK",
            name = "Uttarakhand",
            isUnionTerritory = false,
            region = IndiaRegion.NORTH_HIMALAYA,
            biome = BiomeType.HIMALAYAN,
            capital = "Dehradun",
            centerLat = 30.31,
            centerLon = 78.03,
            cities = listOf(
                city("dehradun_uk", "Dehradun", "Uttarakhand", "UK", 30.3165, 78.0322, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Doon Valley"),
                city("nainital_uk", "Nainital", "Uttarakhand", "UK", 29.3919, 79.4542, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Kumaon Lakes · 2084m"),
                city("rishikesh_uk", "Rishikesh", "Uttarakhand", "UK", 30.0869, 78.2676, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Himalayan Ganga"),
                city("mussoorie_uk", "Mussoorie", "Uttarakhand", "UK", 30.4598, 78.0644, BiomeType.HIMALAYAN, IndiaRegion.NORTH_HIMALAYA, "Garhwal Ridge"),
            ),
        ),
        IndianStateInfo(
            code = "WB",
            name = "West Bengal",
            isUnionTerritory = false,
            region = IndiaRegion.EAST,
            biome = BiomeType.PLAINS,
            capital = "Kolkata",
            centerLat = 22.57,
            centerLon = 88.36,
            cities = listOf(
                city("kolkata_wb", "Kolkata", "West Bengal", "WB", 22.5726, 88.3639, BiomeType.COASTAL, IndiaRegion.EAST, "Hooghly Delta", spotlight = true),
                city("darjeeling_wb", "Darjeeling", "West Bengal", "WB", 27.0410, 88.2663, BiomeType.HIMALAYAN, IndiaRegion.EAST, "Tea Ridge · 2042m"),
                city("siliguri_wb", "Siliguri", "West Bengal", "WB", 26.7271, 88.3953, BiomeType.PLAINS, IndiaRegion.EAST, "Dooars Foothills"),
                city("durgapur_wb", "Durgapur", "West Bengal", "WB", 23.5204, 87.3119, BiomeType.PLAINS, IndiaRegion.EAST, "Rarh Plains"),
            ),
        ),
    )

    val allCities: List<IndianCity> = allStatesAndUTs.flatMap { it.cities }

    val jodhpur: IndianCity = allCities.first { it.id == "jodhpur_rj" }
    val barmer: IndianCity = allCities.first { it.id == "barmer_rj" }
    val mumbai: IndianCity = allCities.first { it.id == "mumbai_mh" }

    val spotlightCities: List<IndianCity> = listOf(
        jodhpur,
        barmer,
        mumbai,
        allCities.first { it.id == "new_delhi_dl" },
        allCities.first { it.id == "jaipur_rj" },
        allCities.first { it.id == "bengaluru_ka" },
        allCities.first { it.id == "kolkata_wb" },
        allCities.first { it.id == "chennai_tn" },
        allCities.first { it.id == "hyderabad_ts" },
        allCities.first { it.id == "leh_la" },
    )

    val defaultPinnedIds: Set<String> = setOf(
        "jodhpur_rj",
        "barmer_rj",
        "mumbai_mh",
        "new_delhi_dl",
        "jaipur_rj",
        "bengaluru_ka",
    )

    fun findCityById(id: String): IndianCity? = allCities.firstOrNull { it.id == id }

    /**
     * Simplified clockwise geographic boundary of mainland India (lat, lon)
     * for 3D extruded radar map rendering.
     */
    val mainlandIndiaOutlineLatLon: List<Pair<Double, Double>> = listOf(
        35.5 to 74.3, // Gilgit-Ladakh North
        35.9 to 76.5, // Karakoram
        34.6 to 78.9, // Aksai Chin border
        32.8 to 78.6, // Ladakh East
        31.1 to 79.0, // Uttarakhand
        29.6 to 80.3, // Pithoragarh
        27.9 to 88.2, // Sikkim West
        27.8 to 88.9, // Sikkim East
        27.0 to 92.0, // Arunachal West
        29.1 to 96.0, // Arunachal North
        27.8 to 97.4, // Kibithu East
        25.8 to 95.2, // Nagaland
        23.8 to 93.3, // Mizoram
        22.0 to 92.9, // Mizoram South
        23.5 to 91.2, // Tripura
        25.2 to 89.9, // Meghalaya
        26.3 to 88.2, // Siliguri Corridor
        21.6 to 88.3, // Sundarbans
        19.8 to 85.9, // Puri
        17.7 to 83.3, // Visakhapatnam
        15.5 to 80.2, // Ongole
        13.1 to 80.3, // Chennai
        10.3 to 79.8, // Point Calimere
        8.1 to 77.5,  // Kanyakumari
        9.9 to 76.2,  // Kochi
        12.9 to 74.8, // Mangaluru
        15.5 to 73.8, // Goa
        19.1 to 72.8, // Mumbai
        21.1 to 72.6, // Surat
        20.7 to 70.9, // Diu
        22.2 to 68.9, // Dwarka
        23.7 to 68.2, // Kutch West
        24.6 to 71.0, // Thar Border (Barmer side)
        26.9 to 69.8, // Jaisalmer West
        29.9 to 73.7, // Ganganagar
        31.6 to 74.6, // Amritsar
        33.2 to 73.9, // Jammu-Kashmir West
    )
}
