# Timed KTail Algorithm (TkT)

## Overview

To mitigate the cost of manually producing and maintaining models capturing software specifications, specification mining techniques can be exploited to automatically derive up-to-date models that faithfully represent the behavior of software systems. So far, specification mining solutions focused on extracting information about the functional behavior of the system, especially in the form of models that represent the ordering of the operations. Well-known examples are finite state models capturing the usage protocol of software interfaces and temporal rules specifying relations among system events.
Although the functional behavior of a software system is a primary aspect of concern, there are several other non-functional characteristics that must be typically addressed jointly with the functional behavior of a software system. Efficiency is one of the most relevant characteristics. In fact, an application delivering the right functionalities inefficiently has a big chance to not satisfy the expectation of its users.

Interestingly, the timing behavior is strongly dependent on the functional behavior of a software system. For instance, the timing of an operation depends on the functional complexity and size of the computation that is performed. Consequently, models that combine the functional and timing behaviors, as well as their dependencies, are extremely important to precisely reason on the behavior of software systems.

The Timed k-Tail (TkT) specification mining technique, addresses the challenge of generating models that capture both the functional and timing behavior of a software system from execution traces.  TkT can mine finite state models that capture such an interplay: the functional behavior is represented by the possible order of the events accepted by the transitions, while the timing behavior is represented through clocks and clock constraints of different nature associated to transitions.

## Download and use

Folder TimedKTail (https://github.com/lta-disco-unimib-it/tkt/tree/master/TimedKTail) contains the source code of TkT.

Folder releases contain pre-compiled versions of TkT, along with zipped tutorials (e.g., https://github.com/lta-disco-unimib-it/tkt/tree/master/TimedKTail/releases/2019.07.10)

Folder TkTTutorial (https://github.com/lta-disco-unimib-it/tkt/tree/master/TimedKTail/TKTTutorial) contains the source of the tutorial 

## Case studies

Compressed folders to replicate the results presented in our papers are available at the following URL https://drive.google.com/open?id=1HOSRLASPz9--U3rEGgeyP_OvrInmX6gP

