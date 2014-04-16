#!/bin/sh

CURRENT_TIME=`date +%Y%m%d%H%M%S`
DEPLOY_DIR=/tmp/deploy/motech/$CURRENT_TIME

mkdir -p $DEPLOY_DIR

cp production.pp $DEPLOY_DIR/production.pp

cd $DEPLOY_DIR

wget https://raw.github.com/motech/motech-scm/master/bootstrap.sh

chmod u+x bootstrap.sh

sudo sh ./bootstrap.sh -c $DEPLOY_DIR/production.pp