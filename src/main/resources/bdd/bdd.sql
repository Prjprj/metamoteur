-- --------------
-- bdd.sql
-- --------------
--
-- This file is part of "Meta-moteur".
--
-- (c) Meta-moteur 2005-2006. All Rights Reserved.
--
-- --LICENSE NOTICE--
-- This program is free software; you can redistribute it and/or
-- modify it under the terms of the GNU General Public License
-- as published by the Free Software Foundation; either version 2
-- of the License, or (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this program; if not, write to the Free Software
-- Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
-- --LICENSE NOTICE--

-- phpMyAdmin SQL Dump
-- version 2.6.2-Debian-3sarge1
-- http://www.phpmyadmin.net
-- 
-- Serveur: localhost
-- Genere le : Jeudi 11 Mai 2006 a 01:12
-- Version du serveur: 4.0.24
-- Version de PHP: 4.3.10-16
-- 
-- Base de donnees: `metamoteur`
-- 

-- --------------------------------------------------------

-- 
-- Structure de la table `bdd`
-- 

CREATE TABLE `bdd` (
  `Uid` int(100) NOT NULL auto_increment,
  `Keywords` varchar(255) NOT NULL default '',
  `Url1` varchar(255) NOT NULL default '',
  `Title1` varchar(255) NOT NULL default '',
  `Desc1` text NOT NULL,
  `Rank1` tinyint(4) NOT NULL default '0',
  `Select1` tinyint(4) NOT NULL default '0',
  `Url2` varchar(255) NOT NULL default '',
  `Title2` varchar(255) NOT NULL default '',
  `Desc2` text NOT NULL,
  `Rank2` tinyint(4) NOT NULL default '0',
  `Select2` tinyint(4) NOT NULL default '0',
  `Url3` varchar(255) NOT NULL default '',
  `Title3` varchar(255) NOT NULL default '',
  `Desc3` text NOT NULL,
  `Rank3` tinyint(4) NOT NULL default '0',
  `Select3` tinyint(4) NOT NULL default '0',
  `Url4` varchar(255) NOT NULL default '',
  `Title4` varchar(255) NOT NULL default '',
  `Desc4` text NOT NULL,
  `Rank4` tinyint(4) NOT NULL default '0',
  `Select4` tinyint(4) NOT NULL default '0',
  `Url5` varchar(255) NOT NULL default '',
  `Title5` varchar(255) NOT NULL default '',
  `Desc5` text NOT NULL,
  `Rank5` tinyint(4) NOT NULL default '0',
  `Select5` tinyint(4) NOT NULL default '0',
  `Url6` varchar(255) NOT NULL default '',
  `Title6` varchar(255) NOT NULL default '',
  `Desc6` text NOT NULL,
  `Rank6` tinyint(4) NOT NULL default '0',
  `Select6` tinyint(4) NOT NULL default '0',
  `Url7` varchar(255) NOT NULL default '',
  `Title7` varchar(255) NOT NULL default '',
  `Desc7` text NOT NULL,
  `Rank7` tinyint(4) NOT NULL default '0',
  `Select7` tinyint(4) NOT NULL default '0',
  `Url8` varchar(255) NOT NULL default '',
  `Title8` varchar(255) NOT NULL default '',
  `Desc8` text NOT NULL,
  `Rank8` tinyint(4) NOT NULL default '0',
  `Select8` tinyint(4) NOT NULL default '0',
  `Url9` varchar(255) NOT NULL default '',
  `Title9` varchar(255) NOT NULL default '',
  `Desc9` text NOT NULL,
  `Rank9` tinyint(4) NOT NULL default '0',
  `Select9` tinyint(4) NOT NULL default '0',
  `Url10` varchar(255) NOT NULL default '',
  `Title10` varchar(255) NOT NULL default '',
  `Desc10` text NOT NULL,
  `Rank10` tinyint(4) NOT NULL default '0',
  `Select10` tinyint(4) NOT NULL default '0',
  `Url11` varchar(255) NOT NULL default '',
  `Title11` varchar(255) NOT NULL default '',
  `Desc11` text NOT NULL,
  `Rank11` tinyint(4) NOT NULL default '0',
  `Select11` tinyint(4) NOT NULL default '0',
  `Url12` varchar(255) NOT NULL default '',
  `Title12` varchar(255) NOT NULL default '',
  `Desc12` text NOT NULL,
  `Rank12` tinyint(4) NOT NULL default '0',
  `Select12` tinyint(4) NOT NULL default '0',
  `Url13` varchar(255) NOT NULL default '',
  `Title13` varchar(255) NOT NULL default '',
  `Desc13` text NOT NULL,
  `Rank13` tinyint(4) NOT NULL default '0',
  `Select13` tinyint(4) NOT NULL default '0',
  `Url14` varchar(255) NOT NULL default '',
  `Title14` varchar(255) NOT NULL default '',
  `Desc14` text NOT NULL,
  `Rank14` tinyint(4) NOT NULL default '0',
  `Select14` tinyint(4) NOT NULL default '0',
  `Url15` varchar(255) NOT NULL default '',
  `Title15` varchar(255) NOT NULL default '',
  `Desc15` text NOT NULL,
  `Rank15` tinyint(4) NOT NULL default '0',
  `Select15` tinyint(4) NOT NULL default '0',
  `Url16` varchar(255) NOT NULL default '',
  `Title16` varchar(255) NOT NULL default '',
  `Desc16` text NOT NULL,
  `Rank16` tinyint(4) NOT NULL default '0',
  `Select16` tinyint(4) NOT NULL default '0',
  `Url17` varchar(255) NOT NULL default '',
  `Title17` varchar(255) NOT NULL default '',
  `Desc17` text NOT NULL,
  `Rank17` tinyint(4) NOT NULL default '0',
  `Select17` tinyint(4) NOT NULL default '0',
  `Url18` varchar(255) NOT NULL default '',
  `Title18` varchar(255) NOT NULL default '',
  `Desc18` text NOT NULL,
  `Rank18` tinyint(4) NOT NULL default '0',
  `Select18` tinyint(4) NOT NULL default '0',
  `Url19` varchar(255) NOT NULL default '',
  `Title19` varchar(255) NOT NULL default '',
  `Desc19` text NOT NULL,
  `Rank19` tinyint(4) NOT NULL default '0',
  `Select19` tinyint(4) NOT NULL default '0',
  `Url20` varchar(255) NOT NULL default '',
  `Title20` varchar(255) NOT NULL default '',
  `Desc20` text NOT NULL,
  `Rank20` tinyint(4) NOT NULL default '0',
  `Select20` tinyint(4) NOT NULL default '0',
  `TimeQuery` varchar(20) NOT NULL default '0',
  PRIMARY KEY  (`Uid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- 
-- Contenu de la table `bdd`
-- 

-- pour tests
INSERT INTO `bdd` VALUES (1, 'dsds', 'dsds', 'sddsds', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '', '', '', 0, 0, '0000-00-00');