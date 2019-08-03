#include "Map.h"
#include "../main/Game.h"

namespace Pixelverse {

Map::Map(): chunkManager(""){
	planet = make_shared<Planet>();
}

Map::~Map(){}

void Map::update(){
	for ( auto &e : entities ) {
		e->applyGravity();
		e->update();
	}
}

void Map::render(){
	Screen::renderPlanet(planet);

	for ( auto &e : entities ) {
		e->render();
	}
}

std::shared_ptr<Region> Map::getRegion(coordinate position){
	return planet->getRegion(position);
}

std::shared_ptr<Chunk> Map::getChunk(coordinate position){
	return planet->getChunk(position);
}

std::shared_ptr<Material> Map::getMaterial(coordinate pixelPos, bool layer){
	return Material::get(getMaterialID(pixelPos, layer));
}

materialID_t Map::getMaterialID(coordinate pixelPos, bool layer){
	return planet->getMaterialID(pixelPos, layer);
}

void Map::setMaterialID(coordinate pixelPos, bool layer, materialID_t id){
	planet->setMaterialID(pixelPos, layer, id);
}

vec2 Map::getGravity(coordinate pixelPos){
	return planet->getGravity(pixelPos);
}

} /* namespace Pixelverse */