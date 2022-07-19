#!/bin/bash

docker compose up -d --build
sbt test && docker compose down
